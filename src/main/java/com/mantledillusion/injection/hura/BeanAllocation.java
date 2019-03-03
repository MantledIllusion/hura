package com.mantledillusion.injection.hura;

import java.io.File;
import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.ClassAllocator;
import com.mantledillusion.injection.hura.Injector.PluginAllocator;
import com.mantledillusion.injection.hura.Injector.InstanceAllocator;
import com.mantledillusion.injection.hura.Injector.ProviderAllocator;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.instruction.Define;

/**
 * Type to use as return types of @{@link Define} annotated {@link Method}s of
 * {@link BlueprintTemplate} implementations.
 * 
 * @param <T>
 *            The type of the allocation. This type is taken as the type to
 *            allocate by {@link Define} annotated, {@link BeanAllocation}
 *            returning {@link Method}s of {@link BlueprintTemplate}
 *            implementations when the implementation is processed by
 *            {@link TypedBlueprint#from(BlueprintTemplate)}.
 */
public final class BeanAllocation<T> {

	/**
	 * Interface for providers of bean instances.
	 * 
	 * @param <T>
	 *            The bean type this {@link BeanProvider} provides.
	 */
	public interface BeanProvider<T> {

		/**
		 * Provides a instance of this provider's bean type.
		 * 
		 * @param callback
		 *            A callback to the injection sequence that caused the call on this
		 *            {@link BeanProvider}. Can be used if the bean being provided needs
		 *            some prerequisite beans; might <b>not</b> be null.
		 * @return A bean instance; might be null
		 */
		T provide(TemporalInjectorCallback callback);
	}

	private final AbstractAllocator<T> allocator;

	private BeanAllocation(AbstractAllocator<T> allocator) {
		this.allocator = allocator;
	}

	AbstractAllocator<T> getAllocator() {
		return allocator;
	}

	/**
	 * Allocate to a given instance that should be used upon injection.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param instance
	 *            The instance to use; might be null.
	 * @return A newly build allocation; never null
	 */
	public static <T> BeanAllocation<T> allocateToInstance(T instance) {
		return new BeanAllocation<>(new InstanceAllocator<>(instance));
	}

	/**
	 * Allocate to a given {@link Class} that should be used upon injection.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param provider
	 *            The {@link BeanProvider} to use; might <b>not</b> be null.
	 * @return A newly build allocation, never null
	 */
	public static <T> BeanAllocation<T> allocateToProvider(BeanProvider<? extends T> provider) {
		if (provider == null) {
			throw new IllegalArgumentException("Unable to allocate a bean to a null provider.");
		}
		return new BeanAllocation<>(new ProviderAllocator<>(provider));
	}

	/**
	 * Allocate to a given {@link Class} that should be used upon injection.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param <T2>
	 *            The allocated type.
	 * @param clazz
	 *            The {@link Class} to use; might <b>not</b> be null.
	 * @param applicators
	 *            The {@link PhasedBeanProcessor}s to apply on every instantiated bean;
	 *            might be null or contain nulls, both is ignored.
	 * @return A newly build allocation, never null
	 */
	@SafeVarargs
	public static final <T, T2 extends T> BeanAllocation<T> allocateToType(Class<T2> clazz, PhasedBeanProcessor<? super T2>... applicators) {
		if (clazz == null) {
			throw new IllegalArgumentException("Unable to allocate a bean to a null class.");
		}
		return new BeanAllocation<>(new ClassAllocator<>(clazz, InjectionProcessors.of(applicators)));
	}

	/**
	 * Allocate to the plugin with the given ID that should be used upon injection.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param directory
	 *            The directory to find the plugin in; might <b>not</b> be null and
	 *            {@link File#isDirectory()} has to return true.
	 * @param pluginId
	 *            The ID of the plugin to use, with which it can be found in the
	 *            given directory; might <b>not</b> be null.
	 * @param applicators
	 *            The {@link PhasedBeanProcessor}s to apply on every instantiated bean;
	 *            might be null or contain nulls, both is ignored.
	 * @return A newly build allocation, never null
	 */
	@SafeVarargs
	public static final <T> BeanAllocation<T> allocateToPlugin(File directory, String pluginId, PhasedBeanProcessor<? super T>... applicators) {
		if (directory == null) {
			throw new IllegalArgumentException("Unable to allocate a bean to a plugin from a null directory.");
		} else if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Unable to allocate a bean to a plugin from a non-directory.");
		} else if (pluginId == null) {
			throw new IllegalArgumentException("Unable to allocate a bean to a plugin with a null ID.");
		}
		return new BeanAllocation<>(new PluginAllocator<>(directory, pluginId, InjectionProcessors.of(applicators)));
	}
}
