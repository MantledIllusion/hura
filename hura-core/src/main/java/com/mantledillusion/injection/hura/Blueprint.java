package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.annotation.instruction.Define;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Interface for a group of {@link Allocation}s.
 * <p>
 * An implementation of this interface may define an arbitrary amount of
 * {@link Allocation} returning {@link Method}s that are annotated with
 * {@link Define}.
 * <p>
 * During processing, these {@link Method}s will be invoked; their returned
 * {@link Allocation} instances will be turned into allocations the
 * {@link Injector} can use during injection;
 * this is how {@link Blueprint} implementations can influence the way
 * an {@link Injector} injects its beans.
 */
public interface Blueprint {

    /**
     * Interface for providers of bean instances.
     *
     * @param <T> The bean type this {@link BeanProvider} provides.
     */
    interface BeanProvider<T> {

        /**
         * Provides a instance of this provider's bean type.
         *
         * @param callback A callback to the injection sequence that caused the call on this
         *                 {@link BeanProvider}. Can be used if the bean being provided needs
         *                 some prerequisite beans; might <b>not</b> be null.
         * @return A bean instance; might be null
         */
        T provide(Injector.TemporalInjectorCallback callback);
    }

    /**
     * Base type for objects that should be treated as given during an injection.
     * <p>
     * Implementations are:
     * <ul>
     * <li>{@link SingletonAllocation}</li>
     * <li>{@link PropertyAllocation}</li>
     * <li>{@link MappingAllocation}</li>
     * </ul>
     */
    abstract class Allocation {

        private Allocation() {
        }
    }

    /**
     * Defines a definite property key-&gt;value pair.
     */
    final class PropertyAllocation extends Allocation {

        private final String key;
        private final String value;

        private PropertyAllocation(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Factory {@link Method} for {@link PropertyAllocation} instances.
         *
         * @param key   The key that identifies the property; might <b>not</b> be null or
         *              empty.
         * @param value The value of the property; might <b>not</b> be null.
         * @return A new {@link PropertyAllocation} instance; never null
         */
        public static PropertyAllocation of(String key, String value) {
            if (StringUtils.isEmpty(key)) {
                throw new IllegalArgumentException("Cannot create property with an empty key");
            } else if (value == null) {
                throw new IllegalArgumentException("Cannot create property with a null value");
            }
            return new PropertyAllocation(key, value);
        }

        /**
         * The {@link PropertyAllocation} key.
         *
         * @return The key; never null
         */
        public String getKey() {
            return key;
        }

        /**
         * The {@link PropertyAllocation} value.
         *
         * @return The value; never null
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Defines a {@link SingletonAllocation} of a specific qualifier.
     */
    final class SingletonAllocation extends Allocation {

        private final String qualifier;
        private final Injector.AbstractAllocator<?> allocator;

        private SingletonAllocation(String qualifier, Injector.AbstractAllocator<?> allocator) {
            this.qualifier = qualifier;
            this.allocator = allocator;
        }

        /**
         * Factory {@link Method} for {@link SingletonAllocation} instances.
         * <p>
         * Allocates the qualifier to the specified instance.
         *
         * @param qualifier The qualifier on whose injections the given instance may be
         *                  referenced at; might <b>not</b> be null.
         * @param bean      The instance to allocate as a {@link SingletonAllocation}; might be null.
         * @return A new {@link SingletonAllocation} instance; never null
         */
        public static SingletonAllocation of(String qualifier, Object bean) {
            if (qualifier == null) {
                throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
            }
            return new SingletonAllocation(qualifier, new Injector.InstanceAllocator<>(bean));
        }

        /**
         * Factory {@link Method} for {@link SingletonAllocation} instances.
         * <p>
         * Allocates the qualifier to the specified {@link BeanProvider}.
         *
         * @param <T>       The type of the singleton.
         * @param qualifier The qualifier on whose injections the given instance may be
         *                  referenced at; might <b>not</b> be null.
         * @param provider  The {@link BeanProvider} to allocate as the provider of a
         *                  {@link SingletonAllocation}; might <b>not</b> be null.
         * @return A new {@link SingletonAllocation} instance; never null
         */
        public static <T> SingletonAllocation of(String qualifier, BeanProvider<T> provider) {
            if (qualifier == null) {
                throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
            } else if (provider == null) {
                throw new IllegalArgumentException("Cannot create singleton with a null provider");
            }
            return new SingletonAllocation(qualifier, new Injector.ProviderAllocator<>(provider));
        }

        /**
         * Factory {@link Method} for {@link SingletonAllocation} instances.
         * <p>
         * Allocates the qualifier to the specified {@link Class}.
         *
         * @param <T>       The type of the singleton.
         * @param qualifier The qualifier on whose injections the given instance may be
         *                  referenced at; might <b>not</b> be null.
         * @param beanClass The {@link Class} to allocate as the type of a {@link SingletonAllocation};
         *                  might <b>not</b> be null.
         * @return A new {@link SingletonAllocation} instance; never null
         */
        public static <T> SingletonAllocation of(String qualifier, Class<T> beanClass) {
            if (qualifier == null) {
                throw new IllegalArgumentException("Cannot create singleton with a null qualifier");
            } else if (beanClass == null) {
                throw new IllegalArgumentException("Cannot create singleton with a null bean class");
            }
            return new SingletonAllocation(qualifier, new Injector.ClassAllocator<>(beanClass, InjectionProcessors.of()));
        }

        /**
         * Factory {@link Method} for {@link SingletonAllocation} instances.
         * <p>
         * Allocates the qualifier to the specified {@link Class}.
         *
         * @param <T>       The type of the singleton.
         * @param qualifier The qualifier on whose injections the given instance may be
         *                  referenced at; might <b>not</b> be null.
         * @param beanClass The {@link Class} of the {@link SingletonAllocation} to find a plugin for;
         *                  might <b>not</b> be null.
         * @param directory The directory to find the plugin in; might <b>not</b> be null and
         *                  {@link File#isDirectory()} has to return true.
         * @param pluginId  The ID of the plugin to use, with which it can be found in the
         *                  given directory; might <b>not</b> be null.
         * @return A new {@link SingletonAllocation} instance; never null
         */
        public static <T> SingletonAllocation of(String qualifier, Class<T> beanClass, File directory, String pluginId) {
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
            return new SingletonAllocation(qualifier, new Injector.PluginAllocator<>(directory, pluginId, beanClass));
        }

        /**
         * Returns the qualifier of this singleton.
         *
         * @return The qualifier; never null or empty
         */
        public String getQualifier() {
            return qualifier;
        }

        Injector.AbstractAllocator<?> getAllocator() {
            return allocator;
        }
    }

    /**
     * Defines an ID mapping of {@link SingletonAllocation} IDs, from a base to a target.
     */
    final class MappingAllocation extends Allocation {

        private final String base;
        private final String target;

        private MappingAllocation(String base, String target) {
            this.base = base;
            this.target = target;
        }

        /**
         * Factory {@link Method} for {@link MappingAllocation} instances.
         *
         * @param base   The qualifier that is mapped. SingletonAllocation references to this
         *               mapping base ID will reference the mapping target singleton
         *               afterwards; might <b>not</b> be null.
         * @param target The qualifier that is mapped to. SingletonAllocation references to the
         *               mapping base ID will reference this mapping target ID's singleton
         *               afterwards; might <b>not</b> be null.
         * @return A new {@link MappingAllocation} instance; never null
         */
        public static MappingAllocation of(String base, String target) {
            if (StringUtils.isEmpty(base)) {
                throw new IllegalArgumentException("Cannot create a singleton mapping with a null qualifier");
            } else if (target == null) {
                throw new IllegalArgumentException("Cannot create a singleton mapping with a null mapping target");
            }
            return new MappingAllocation(base, target);
        }

        /**
         * Returns the base qualifier to getMapping.
         *
         * @return The base qualifier; never null
         */
        public String getBase() {
            return base;
        }

        /**
         * Returns the target qualifier to getMapping to;
         *
         * @return The target qualifier; never null.
         */
        public String getTarget() {
            return target;
        }
    }

    /**
     * Type to use as return types of @{@link Define} annotated {@link Method}s of
     * {@link Blueprint} implementations.
     */
    final class TypeAllocation extends Allocation {

        private final Class<?> type;
        private final Injector.AbstractAllocator<?> allocator;

        private <T> TypeAllocation(Class<T> type, Injector.AbstractAllocator<T> allocator) {
            this.type = type;
            this.allocator = allocator;
        }

        /**
         * Allocate to a given instance that should be used upon injection.
         *
         * @param <T>      The bean type.
         * @param type     The {@link Class} of the bean type; might not be null.
         * @param instance The instance to use; might be null.
         * @return A newly build allocation; never null
         */
        public static <T> TypeAllocation allocateToInstance(Class<T> type, T instance) {
            if (type == null) {
                throw new IllegalArgumentException("Unable to allocate a null type.");
            }
            return new TypeAllocation(type, new Injector.InstanceAllocator<>(instance));
        }

        /**
         * Allocate to a given {@link Class} that should be used upon injection.
         *
         * @param <T>      The bean type.
         * @param type     The {@link Class} of the bean type; might not be null.
         * @param provider The {@link BeanProvider} to use; might <b>not</b> be null.
         * @return A newly build allocation, never null
         */
        public static <T> TypeAllocation allocateToProvider(Class<T> type, BeanProvider<? extends T> provider) {
            if (type == null) {
                throw new IllegalArgumentException("Unable to allocate a null type.");
            } else if (provider == null) {
                throw new IllegalArgumentException("Unable to allocate a bean to a null provider.");
            }
            return new TypeAllocation(type, new Injector.ProviderAllocator<>(provider));
        }

        /**
         * Allocate to a given {@link Class} that should be used upon injection.
         *
         * @param <T>        The bean type.
         * @param <T2>       The allocated type.
         * @param type       The {@link Class} of the bean type; might not be null.
         * @param clazz      The {@link Class} to use; might <b>not</b> be null.
         * @param processors The {@link PhasedBeanProcessor}s to apply on every instantiated bean;
         *                   might be null or contain nulls, both is ignored.
         * @return A newly build allocation, never null
         */
        @SafeVarargs
        public static final <T, T2 extends T> TypeAllocation allocateToType(Class<T> type, Class<T2> clazz, PhasedBeanProcessor<? super T2>... processors) {
            if (type == null) {
                throw new IllegalArgumentException("Unable to allocate a null type.");
            } else if (clazz == null) {
                throw new IllegalArgumentException("Unable to allocate a bean to a null class.");
            }
            return new TypeAllocation(type, new Injector.ClassAllocator<>(clazz, InjectionProcessors.of(processors)));
        }

        /**
         * Allocate to the plugin with the given ID that should be used upon injection.
         *
         * @param <T>        The bean type.
         * @param type       The {@link Class} of the bean type; might not be null.
         * @param directory  The directory to find the plugin in; might <b>not</b> be null and
         *                   {@link File#isDirectory()} has to return true.
         * @param pluginId   The ID of the plugin to use, with which it can be found in the
         *                   given directory; might <b>not</b> be null.
         * @param processors The {@link PhasedBeanProcessor}s to apply on every instantiated bean;
         *                   might be null or contain nulls, both is ignored.
         * @return A newly build allocation, never null
         */
        @SafeVarargs
        public static final <T> TypeAllocation allocateToPlugin(Class<T> type, File directory, String pluginId, PhasedBeanProcessor<? super T>... processors) {
            if (type == null) {
                throw new IllegalArgumentException("Unable to allocate a null type.");
            } else if (directory == null) {
                throw new IllegalArgumentException("Unable to allocate a bean to a plugin from a null directory.");
            } else if (!directory.isDirectory()) {
                throw new IllegalArgumentException("Unable to allocate a bean to a plugin from a non-directory.");
            } else if (pluginId == null) {
                throw new IllegalArgumentException("Unable to allocate a bean to a plugin with a null ID.");
            }
            return new TypeAllocation(type, new Injector.PluginAllocator<>(directory, pluginId, InjectionProcessors.of(processors)));
        }

        Class<?> getType() {
            return type;
        }

        Injector.AbstractAllocator<?> getAllocator() {
            return allocator;
        }
    }
}
