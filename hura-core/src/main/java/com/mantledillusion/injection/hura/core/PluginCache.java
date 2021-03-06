package com.mantledillusion.injection.hura.core;

import com.mantledillusion.cache.hydnora.HydnoraCache;
import com.mantledillusion.essentials.concurrency.locks.LockIdentifier;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.exception.PluginException;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.SourceVersion;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class PluginCache {

	private static final String SPI_DIR = "META-INF/services/";
	private static final String FILE_EXTENSION_JAR = ".jar";
	private static final String FILE_INFIX_VERSION = "_v";
	private static final String FILE_SUFFIX_VERSION = FILE_INFIX_VERSION + Plugin.VERSION_PATTERN;
	private static final String REGEX_PLUGINID_JAR = ".+\\"+FILE_EXTENSION_JAR;
	private static final String REGEX_PLUGINID_VERSION = ".+" + FILE_SUFFIX_VERSION;

	private static class PluginId extends LockIdentifier {

		private final File directory;
		private final String pluginName;
		private final int[] version;

		private PluginId(String checksum, File directory, String pluginName, int[] version) {
			super(checksum);
			this.directory = directory;
			this.pluginName = pluginName;
			this.version = version;
		}

		private File toFile() {
			return new File(toString());
		}

		@Override
		public String toString() {
			return toPath(this.directory, this.pluginName + (version.length > 0 ?
					FILE_INFIX_VERSION + StringUtils.join(this.version, '.') : StringUtils.EMPTY));
		}

		private static String toPath(File directory, String fileName) {
			return directory.getAbsolutePath() + "/" + fileName + FILE_EXTENSION_JAR;
		}
		
		private static PluginId from(File directory, String fileName) {
			String checksum;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(Files.readAllBytes(Paths.get(new File(toPath(directory, fileName)).toURI())));
				StringBuilder sb = new StringBuilder();
				for (byte b : md.digest()) {
					sb.append(String.format("%02x", b));
				}
				checksum = sb.toString().toUpperCase();
			} catch (NoSuchAlgorithmException | IOException e) {
				throw new PluginException("Unable to create hash for plugin '"+fileName+"'", e);
			}

			if (fileName.matches(".*" + FILE_SUFFIX_VERSION)) {
				int versionIdx = fileName.lastIndexOf(FILE_INFIX_VERSION);
				return new PluginId(checksum, directory, fileName.substring(0, versionIdx),
						InjectionUtils.parseVersion(fileName.substring(versionIdx + 2)));
			}
			return new PluginId(checksum, directory, fileName, new int[0]);
		}

	}
	
	static final class PluginClassLoader extends URLClassLoader {

		private final ReflectionCache pluginReflectionCache = new ReflectionCache();
		private final Map<Class<?>, List<Class<?>>> pluggables = new HashMap<>();
		
		private PluginClassLoader(PluginId id) throws MalformedURLException {
			super(new URL[] { id.toFile().toURI().toURL() }, Injector.class.getClassLoader());
		}

		ReflectionCache getPluginReflectionCache() {
			return this.pluginReflectionCache;
		}
	}

	private static final class PluggableCache extends HydnoraCache<PluginId, PluginClassLoader> {

		private PluggableCache() {
			super(ReferenceMode.WEAK);
			setWrapRuntimeExceptions(false);
		}

		@SuppressWarnings("unchecked")
		private <T> Class<T> retrieve(PluginId id, Class<? super T> spiType) {
			return get(id, plugin -> {
				if (!plugin.pluggables.containsKey(spiType)) {
					throw new PluginException("The plugin '" + id + "' does not offer a pluggable implementing the SPI '"
							+ spiType.getName() + "'");
				}

				List<Class<?>> pluggableTypes = plugin.pluggables.get(spiType);
				if (pluggableTypes.size() > 1) {
					throw new PluginException("The plugin '" + id + "' provides " + pluggableTypes.size() + " pluggables implementing the SPI '"
							+ spiType.getName() + "'; cannot decide which one to inject");
				}

				return (Class<T>) pluggableTypes.get(0);
			});
		}

		@Override
		@SuppressWarnings("resource")
		protected PluginClassLoader load(PluginId id) throws Exception {
			PluginClassLoader pluginClassLoader = new PluginClassLoader(id);

			try {
				Enumeration<JarEntry> e = new JarFile(id.toFile()).entries();
				while (e.hasMoreElements()) {
					JarEntry entry = e.nextElement();
					if (!entry.isDirectory() && entry.getName().startsWith(SPI_DIR)) {
						String spiClassName = entry.getName().substring(SPI_DIR.length());
						Class<?> spiClass;
						if (!SourceVersion.isName(spiClassName)) {
							throw new PluginException("The .jar '" + id + "' is no valid plugin; its service provider file '"
									+ entry.getName() + "' does not have the name of a valid class name");
						} else {
							try {
								spiClass = pluginClassLoader.loadClass(spiClassName);
							} catch (ClassNotFoundException e2) {
								throw new PluginException("The .jar '" + id + "' is no valid plugin; its service provider file '"
										+ entry.getName() + "' refers to an SPI class that is unknown to the class loader of the "
										+ ".jar itself as well as all parent class loaders", e2);
							}
						}

						InputStream spiFileStream = pluginClassLoader.getResourceAsStream(entry.getName());
						try(BufferedReader br = new BufferedReader(new InputStreamReader(spiFileStream))) {
							for(String line; (line = br.readLine()) != null; ) {

								Class<?> spClass;
								if (!SourceVersion.isName(line)) {
									throw new PluginException("The .jar '" + id + "' is no valid plugin; its service provider file '"
											+ entry.getName() + "' specifies the service provider '"+line+"' which is no valid class name");
								} else {
									try {
										spClass = pluginClassLoader.loadClass(line);
									} catch (ClassNotFoundException e2) {
										throw new PluginException("The .jar '" + id + "' is no valid plugin; its service provider file '"
												+ entry.getName() + "' specifies the service provider '"+line+"' which cannot be loaded", e2);
									}
								}

								if (!spiClass.isAssignableFrom(spClass)) {
									throw new PluginException("The .jar '" + id + "' is no valid plugin; its service provider file '"
											+ entry.getName() + "' specifies the service provider '"+line+"' which is no implementation of "+spiClassName);
								}

								pluginClassLoader.pluggables.computeIfAbsent(spiClass, c -> new ArrayList<>()).add(spClass);
							}
						}
					}
				}
				
				return pluginClassLoader;
			} catch (Exception e) {
				boolean closedPluginClassloader = true;
				try {
					pluginClassLoader.close();
				} catch (IOException e2) {
					closedPluginClassloader = false;
				}

				throw new PluginException("Unable to load plugin '" + id + "' (closing the plugin's classloader "
						+ (closedPluginClassloader ? "succeeded" : "failed") + ")", e);
			}
		}
	}

	private static final PluggableCache CACHE = new PluggableCache();

	private PluginCache() {
	}

	static <T, T2 extends T> Class<T2> findPluggable(File directory, String pluginId, Class<T> spiType,
													 int[] versionFrom, int[] versionUntil) {
	    if (pluginId.matches(REGEX_PLUGINID_JAR)) {
			throw new PluginException("Cannot load a plugin for the SPI '" + spiType.getName()
					+ "'; the pluginId '" + pluginId + "' ends with a " + FILE_EXTENSION_JAR
					+ " extension, which has to be appended automatically by the plugin loader");
		} else if (pluginId.matches(REGEX_PLUGINID_VERSION)) {
			throw new PluginException("Cannot load a plugin for the SPI '" + spiType.getName()
					+ "'; the pluginId '" + pluginId + "' ends with a version, but plugins "
					+ "should always be requested in unversioned manner");
		} else if (spiType.getClassLoader() instanceof PluginClassLoader) {
	        throw new PluginException("Cannot load a plugin for the SPI '" + spiType.getName()
                    + "'; the type originates from a plugin itself and plugins cannot be chained");
        }

		PluginId foundPlugin = null;
		for (File file : directory.listFiles(
				(dir, name) -> name.startsWith(pluginId) && name.endsWith(FILE_EXTENSION_JAR))) {
			String fileName = file.getName();
			String version = fileName.substring(pluginId.length(), fileName.length() - 4);

			if (version.isEmpty() || version.matches(FILE_SUFFIX_VERSION)) {
				PluginId filePlugin = PluginId.from(directory, fileName.substring(0, fileName.length() - 4));
				if (InjectionUtils.compareVersions(filePlugin.version, versionFrom) >= 0 &&
						InjectionUtils.compareVersions(filePlugin.version, versionUntil) <0 &&
						(foundPlugin == null || InjectionUtils.compareVersions(filePlugin.version, foundPlugin.version) > 0)) {
					foundPlugin = filePlugin;
				}
			}
		}

		if (foundPlugin == null) {
			throw new PluginException("Unable to find a name-matching " + FILE_EXTENSION_JAR + " file for the plugin '"
					+ pluginId + "' in the directory '" + directory.getAbsolutePath() + "'");
		}

		return CACHE.retrieve(foundPlugin, spiType);
	}

}
