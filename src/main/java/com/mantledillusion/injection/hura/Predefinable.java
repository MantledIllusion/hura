package com.mantledillusion.injection.hura;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.BeanAllocation.BeanProvider;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.InstanceAllocator;
import com.mantledillusion.injection.hura.Injector.ProviderAllocator;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;
import com.mantledillusion.injection.hura.Injector.ClassAllocator;

/**
 * Base type for objects that should be treated as given during an injection.
 * <p>
 * Implementations are:<br>
 * - {@link Property}<br>
 * - {@link Singleton}<br>
 * - {@link SingletonMapping}<br>
 */
public abstract class Predefinable {

	/**
	 * Defines a definite property key-&gt;value pair.
	 */
	public static final class Property extends Predefinable {

		private final String propertyKey;
		private final String propertyValue;

		private Property(String propertyKey, String propertyValue) {
			this.propertyKey = propertyKey;
			this.propertyValue = propertyValue;
		}

		public String getKey() {
			return propertyKey;
		}

		public String getValue() {
			return propertyValue;
		}

		/**
		 * Factory {@link Method} for {@link Property} instances.
		 * 
		 * @param propertyKey
		 *            The key that identifies the property; may <b>not</b> be null or
		 *            empty.
		 * @param propertyValue
		 *            The value of the property; may <b>not</b> be null.
		 * @return A new {@link Property} instance; never null
		 */
		public static Property of(String propertyKey, String propertyValue) {
			if (StringUtils.isEmpty(propertyKey)) {
				throw new IllegalArgumentException("Cannot create property with empty key");
			} else if (propertyValue == null) {
				throw new IllegalArgumentException("Cannot create property with null value");
			}
			return new Property(propertyKey, propertyValue);
		}
	}

	/**
	 * Defines an already instantiated and injected bean that has to be used as a
	 * {@link Singleton} on injections of a specifiable singletonId.
	 */
	public static final class Singleton extends Predefinable {

		private final String singletonId;
		private final AbstractAllocator<?> allocator;

		private Singleton(String singletonId, AbstractAllocator<?> allocator) {
			this.singletonId = singletonId;
			this.allocator = allocator;
		}

		/**
		 * Returns the singletonId of this singleton.
		 * 
		 * @return The singletonId; never null or empty
		 */
		public String getSingletonId() {
			return singletonId;
		}

		AbstractAllocator<?> getAllocator() {
			return allocator;
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the singletonId to the specified instance.
		 * 
		 * @param singletonId
		 *            The singletonId on whose injections the given instance may be
		 *            referenced at; may <b>not</b> be null.
		 * @param bean
		 *            The instance to allocate as a {@link Singleton}; may be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static Singleton of(String singletonId, Object bean) {
			if (singletonId == null) {
				throw new IllegalArgumentException("Cannot create singleton with null singletonId");
			}
			return new Singleton(singletonId, new InstanceAllocator<>(bean));
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the singletonId to the specified {@link BeanProvider}.
		 * 
		 * @param <T>
		 *            The type of the singleton.
		 * @param singletonId
		 *            The singletonId on whose injections the given instance may be
		 *            referenced at; may <b>not</b> be null.
		 * @param provider
		 *            The {@link BeanProvider} to allocate as the provider of a
		 *            {@link Singleton}; may <b>not</b> be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static <T> Singleton of(String singletonId, BeanProvider<T> provider) {
			if (singletonId == null) {
				throw new IllegalArgumentException("Cannot create singleton with null singletonId");
			} else if (provider == null) {
				throw new IllegalArgumentException("Cannot create singleton with null provider");
			}
			return new Singleton(singletonId, new ProviderAllocator<>(provider));
		}

		/**
		 * Factory {@link Method} for {@link Singleton} instances.
		 * <p>
		 * Allocates the singletonId to the specified {@link Class}.
		 * 
		 * @param <T>
		 *            The type of the singleton.
		 * @param singletonId
		 *            The singletonId on whose injections the given instance may be
		 *            referenced at; may <b>not</b> be null.
		 * @param beanClass
		 *            The {@link Class} to allocate as the type of a {@link Singleton};
		 *            may <b>not</b> be null.
		 * @return A new {@link Singleton} instance; never null
		 */
		public static <T> Singleton of(String singletonId, Class<T> beanClass) {
			if (singletonId == null) {
				throw new IllegalArgumentException("Cannot create singleton with null singletonId");
			} else if (beanClass == null) {
				throw new IllegalArgumentException("Cannot create singleton with null bean class");
			}
			return new Singleton(singletonId, new ClassAllocator<>(beanClass, InjectionProcessors.of()));
		}
	}

	/**
	 * Defines an ID mapping of {@link Singleton}, from a singletonId to a target.
	 */
	public static final class SingletonMapping extends Predefinable {

		private final String mappingBase;
		private final String mappingTarget;
		private final SingletonMode mode;

		private SingletonMapping(String mappingBase, String mappingTarget, SingletonMode mode) {
			this.mappingBase = mappingBase;
			this.mappingTarget = mappingTarget;
			this.mode = mode;
		}

		public String getMappingBase() {
			return mappingBase;
		}

		public String getMappingTarget() {
			return mappingTarget;
		}

		public SingletonMode getMode() {
			return mode;
		}

		/**
		 * Factory {@link Method} for {@link SingletonMapping} instances.
		 * 
		 * @param mappingBase
		 *            The singletonId that is mapped. Singleton references to this
		 *            mapping base ID will reference the mapping target singleton
		 *            afterwards; might <b>not</b> be null.
		 * @param mappingTarget
		 *            The singletonId that is mapped to. Singleton references to the
		 *            mapping base ID will reference this mapping target ID's singleton
		 *            afterwards; might <b>not</b> be null.
		 * @param mode
		 *            The {@link SingletonMode} that determines which pool's
		 *            {@link Singleton}s this mapping refers to; might <b>not</b> be
		 *            null.
		 * @return A new {@link SingletonMapping} instance; never null
		 */
		public static SingletonMapping of(String mappingBase, String mappingTarget, SingletonMode mode) {
			if (StringUtils.isEmpty(mappingBase)) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null singletonId");
			} else if (mappingTarget == null) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null mapping target");
			} else if (mode == null) {
				throw new IllegalArgumentException("Cannot create a singleton mapping with a null mode");
			}
			return new SingletonMapping(mappingBase, mappingTarget, mode);
		}
	}

	private Predefinable() {
	}
}
