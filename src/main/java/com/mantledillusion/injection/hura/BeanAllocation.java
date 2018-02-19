package com.mantledillusion.injection.hura;

import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.ClassAllocator;
import com.mantledillusion.injection.hura.Injector.InstanceAllocator;
import com.mantledillusion.injection.hura.Injector.ProviderAllocator;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.Define;

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
	 * @param clazz
	 *            The {@link Class} to use; might <b>not</b> be null.
	 * @param applicators
	 *            The {@link PhasedProcessor}s to apply on every instantiated bean;
	 *            might be null or contain nulls, both is ignored.
	 * @return A newly build allocation, never null
	 */
	@SafeVarargs
	public static final <T> BeanAllocation<T> allocateToType(Class<? extends T> clazz,
			PhasedProcessor<? super T>... applicators) {
		if (clazz == null) {
			throw new IllegalArgumentException("Unable to allocate a bean to a null class.");
		}
		return new BeanAllocation<>(new ClassAllocator<>(clazz, InjectionProcessors.of(applicators)));
	}
}
