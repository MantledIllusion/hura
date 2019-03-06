package com.mantledillusion.injection.hura;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.BeanAllocation.BeanProvider;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.InstanceAllocator;
import com.mantledillusion.injection.hura.Injector.ProviderAllocator;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;
import com.mantledillusion.injection.hura.Injector.ClassAllocator;

/**
 * Base type for objects that should be treated as given during an injection.
 * <p>
 * Implementations are:
 * <ul>
 * <li>{@link Property}</li>
 * <li>{@link Singleton}</li>
 * <li>{@link Mapping}</li>
 * </ul>
 * - {@link Mapping}<br>
 */
public abstract class Predefinable {

	/**
	 * Defines a definite {@link Property} key-&gt;value pair.
	 */
	public static final class Property extends Predefinable {

		private final String key;
		private final String value;

		private Property(String key, String value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * The {@link Property} key.
		 * 
		 * @return The key; never null
		 */
		public String getKey() {
			return key;
		}

		/**
		 * The {@link Property} value.
		 * 
		 * @return The value; never null
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Factory {@link Method} for {@link Property} instances.
		 * 
		 * @param key
		 *            The key that identifies the property; might <b>not</b> be null or
		 *            empty.
		 * @param value
		 *            The value of the property; might <b>not</b> be null.
		 * @return A new {@link Property} instance; never null
		 */
		public static Property of(String key, String value) {
			if (StringUtils.isEmpty(key)) {
				throw new IllegalArgumentException("Cannot create property with an empty key");
			} else if (value == null) {
				throw new IllegalArgumentException("Cannot create property with a null value");
			}
			return new Property(key, value);
		}
	}

	/**
	 * Defines a {@link Singleton} of a specific qualifier.
	 */
	public static final class Singleton extends Predefinable {

		private final String qualifier;
		private final AbstractAllocator<?> allocator;

		private Singleton(String qualifier, AbstractAllocator<?> allocator) {
			this.qualifier = qualifier;
			this.allocator = allocator;
		}

		/**
		 * Returns the qualifier of this singleton.
		 * 
		 * @return The qualifier; never null or empty
		 */
		public String getQualifier() {
			return qualifier;
		}

		AbstractAllocator<?> getAllocator() {
			return allocator;
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the qualifier to the specified instance.
		 * 
		 * @param qualifier
		 *            The qualifier on whose injections the given instance may be
		 *            referenced at; might <b>not</b> be null.
		 * @param bean
		 *            The instance to allocate as a {@link Singleton}; might be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static Singleton of(String qualifier, Object bean) {
			if (qualifier == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
			}
			return new Singleton(qualifier, new InstanceAllocator<>(bean));
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the qualifier to the specified {@link BeanProvider}.
		 * 
		 * @param <T>
		 *            The type of the singleton.
		 * @param qualifier
		 *            The qualifier on whose injections the given instance may be
		 *            referenced at; might <b>not</b> be null.
		 * @param provider
		 *            The {@link BeanProvider} to allocate as the provider of a
		 *            {@link Singleton}; might <b>not</b> be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static <T> Singleton of(String qualifier, BeanProvider<T> provider) {
			if (qualifier == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
			} else if (provider == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null provider");
			}
			return new Singleton(qualifier, new ProviderAllocator<>(provider));
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the qualifier to the specified {@link Class}.
		 *
		 * @param <T>
		 *            The type of the singleton.
		 * @param qualifier
		 *            The qualifier on whose injections the given instance may be
		 *            referenced at; might <b>not</b> be null.
		 * @param beanClass
		 *            The {@link Class} to allocate as the type of a {@link Singleton};
		 *            might <b>not</b> be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static <T> Singleton of(String qualifier, Class<T> beanClass) {
			if (qualifier == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
			} else if (beanClass == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null bean class");
			}
			return new Singleton(qualifier, new ClassAllocator<>(beanClass, InjectionProcessors.of()));
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the qualifier to the specified {@link Class}.
		 *
		 * @param <T>
		 *            The type of the singleton.
		 * @param qualifier
		 *            The qualifier on whose injections the given instance may be
		 *            referenced at; might <b>not</b> be null.
		 * @param beanClass
		 *            The {@link Class} of the {@link Singleton} to find a plugin for;
		 *            might <b>not</b> be null.
		 * @param directory
		 *            The directory to find the plugin in; might <b>not</b> be null and
		 *            {@link File#isDirectory()} has to return true.
		 * @param pluginId
		 *            The ID of the plugin to use, with which it can be found in the
		 *            given directory; might <b>not</b> be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static <T> Singleton of(String qualifier, Class<T> beanClass, File directory, String pluginId) {
			if (qualifier == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
			} else if (beanClass == null) {
				throw new IllegalArgumentException("Cannot create singleton with a null bean class");
			} else if (directory == null) {
				throw new IllegalArgumentException("Cannot create singleton with a plugin from a null directory.");
			} else if (!directory.isDirectory()) {
				throw new IllegalArgumentException("Cannot create singleton with a plugin from a non-directory.");
			} else if (pluginId == null) {
				throw new IllegalArgumentException("Cannot create singleton with a plugin with a null ID.");
			}
			return new Singleton(qualifier, new Injector.PluginAllocator<>(directory, pluginId, beanClass));
		}
	}

	/**
	 * Defines an ID mapping of {@link Singleton} IDs, from a base to a target.
	 */
	public static final class Mapping extends Predefinable {

		private final String base;
		private final String target;
		private final SingletonMode mode;

		private Mapping(String base, String target, SingletonMode mode) {
			this.base = base;
			this.target = target;
			this.mode = mode;
		}

		/**
		 * Returns the base qualifier to map.
		 * 
		 * @return The base qualifier; never null
		 */
		public String getBase() {
			return base;
		}

		/**
		 * Returns the target qualifier to map to;
		 * 
		 * @return The target qualifier; never null.
		 */
		public String getTarget() {
			return target;
		}

		/**
		 * Returns the {@link Singleton} pool this {@link Mapping} refers to.
		 * 
		 * @return The {@link SingletonMode}; never null
		 */
		public SingletonMode getMode() {
			return mode;
		}

		/**
		 * Factory {@link Method} for {@link Mapping} instances.
		 * 
		 * @param base
		 *            The qualifier that is mapped. Singleton references to this
		 *            mapping base ID will reference the mapping target singleton
		 *            afterwards; might <b>not</b> be null.
		 * @param target
		 *            The qualifier that is mapped to. Singleton references to the
		 *            mapping base ID will reference this mapping target ID's singleton
		 *            afterwards; might <b>not</b> be null.
		 * @param mode
		 *            The {@link SingletonMode} that determines which pool's
		 *            {@link Singleton}s this mapping refers to; might <b>not</b> be
		 *            null.
		 * @return A new {@link Mapping} instance; never null
		 */
		public static Mapping of(String base, String target, SingletonMode mode) {
			if (StringUtils.isEmpty(base)) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null qualifier");
			} else if (target == null) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null mapping target");
			} else if (mode == null) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null mode");
			}
			return new Mapping(base, target, mode);
		}
	}

	private Predefinable() {
	}
}
