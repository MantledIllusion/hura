package com.mantledillusion.injection.hura;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.StreamSupport;

import com.mantledillusion.cache.hydnora.HydnoraCache;
import com.mantledillusion.essentials.concurrency.locks.LockIdentifier;
import com.mantledillusion.injection.hura.exception.PluginException;

import javax.lang.model.SourceVersion;

final class PluginCache {

	private static final String SPI_DIR = "META-INF/services/";
	private static final String FILE_EXTENSION_JAR = ".jar";
	private static final String FILE_SUFFIX_VERSION = "_v[1-9][0-9]*";

	static final class PluginId extends LockIdentifier {

		private final File directory;
		private final String pluginName;
		private final int version;

		private PluginId(File directory, String pluginName, int version) {
			super(pluginName, version);
			this.directory = directory;
			this.pluginName = pluginName;
			this.version = version;
		}

		private boolean isVersioned() {
			return this.version > 0;
		}

		private File toFile() {
			return new File(toString());
		}

		@Override
		public String toString() {
			return directory.getAbsolutePath() + "/" + pluginName
					+ (this.version > 0 ? "_v" + this.version + FILE_EXTENSION_JAR : FILE_EXTENSION_JAR);
		}
		
		private static PluginId from(File directory, String fileName) {
			if (fileName.matches(".*" + FILE_SUFFIX_VERSION)) {
				int versionIdx = fileName.lastIndexOf("_v");
				return new PluginId(directory, fileName.substring(0, versionIdx),
						Integer.parseInt(fileName.substring(versionIdx + 2)));
			}
			return new PluginId(directory, fileName, 0);
		}
	}
	
	static final class PluginClassLoader extends URLClassLoader {

		private final PluginId id;
		
		private PluginClassLoader(PluginId id) throws MalformedURLException {
			super(new URL[] { id.toFile().toURI().toURL() }, Injector.class.getClassLoader());
			this.id = id;
		}
		
		PluginId getPluginId() {
			return this.id;
		}
	}

	private static final class Plugin {

		private final Map<Class<?>, List<Class<?>>> pluggables;

		private Plugin(Map<Class<?>, List<Class<?>>> pluggables) {
			this.pluggables = Collections.unmodifiableMap(pluggables);
		}
	}

	private static final class PluggableCache extends HydnoraCache<Plugin, PluginId> {

		@SuppressWarnings("unchecked")
		private <T> Class<T> retrieve(PluginId id, Class<? super T> pluggableType) {
			return get(id, plugin -> {
				if (plugin.pluggables.containsKey(pluggableType)) {
					return (Class<T>) plugin.pluggables.get(pluggableType).get(0); // TODO really use the next best?
				}

				throw new PluginException("The plugin '" + id + "' does not offer a pluggable implementing '"
						+ pluggableType.getName() + "'");
			});
		}

		@Override
		@SuppressWarnings("resource")
		protected Plugin load(PluginId id) throws Exception {
			PluginClassLoader pluginClassLoader = new PluginClassLoader(id);

			try {
				Map<Class<?>, List<Class<?>>> pluggables = new HashMap<>();
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
								spiClass = pluginClassLoader.getParent().loadClass(spiClassName);
							} catch (ClassNotFoundException e2) {
								continue; // If the parent class loader does not know the class the service providers implement, th
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

								pluggables.computeIfAbsent(spiClass, c -> new ArrayList<>()).add(spClass);
							}
						}
					}
				}

				if (id.isVersioned()) {
					for (int i=id.version-1; i>= 0; i++) {
						outdate(new PluginId(id.directory, id.pluginName, i));
					}
				}
				
				return new Plugin(pluggables);
			} catch (Exception e) {
				// TODO: here: trigger ReflectionCache to invalidate everything with the given pluginId
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
		
		private void outdate(PluginId id) {
			invalidate(id, plugin -> {
				// TODO: in ReflectionCache: make it remember all plugin classes by their plugin id; ((PluginClassLoader) object.getClass().getClassLoader()).getPluginId()
				// TODO: here: trigger ReflectionCache to invalidate everything with the given pluginId
				return true;
			});
		}
	}

	private static final PluggableCache CACHE = new PluggableCache();

	private PluginCache() {
	}

	static <T, T2 extends T> Class<T2> findPluggable(File directory, String pluginId, Class<T> pluggableType) {
		PluginId requiredPlugin = PluginId.from(directory, pluginId);

		PluginId foundPlugin = null;
		if (requiredPlugin.isVersioned()) {
			throw new PluginException("Cannot load a specific version of a plugin (requested: plugin '"
					+ requiredPlugin.pluginName + "' version " + requiredPlugin.version
					+ "); plugins should always be requested in unversioned manner.");
		} else {
			for (File file : directory
					.listFiles((dir, name) -> name.startsWith(pluginId) && name.endsWith(FILE_EXTENSION_JAR))) {
				String fileName = file.getName();
				String version = fileName.substring(pluginId.length(), fileName.length() - 4);

				if (version.isEmpty() || version.matches(FILE_SUFFIX_VERSION)) {
					PluginId filePlugin = PluginId.from(directory, fileName.substring(0, fileName.length() - 4));
					if (foundPlugin == null || foundPlugin.version < filePlugin.version) {
						foundPlugin = filePlugin;
					}
				}
			}
		}

		if (foundPlugin != null) {
			return CACHE.retrieve(foundPlugin, pluggableType);
		}

		throw new PluginException("Unable to find a name-matching " + FILE_EXTENSION_JAR + " file for the plugin '"
				+ pluginId + "' in the directory '" + directory.getAbsolutePath() + "'");
	}
}
