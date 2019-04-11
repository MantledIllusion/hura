package com.mantledillusion.injection.hura.annotation.instruction;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.property.Property;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.exception.InjectionException;

/**
 * Extension {@link Annotation} to @{@link Inject} and @{@link Property}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Inject}
 * and @{@link Optional} will only be injected if the injection is explicitly
 * anticipated as described in {@link InjectionMode#EXPLICIT}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Property}
 * and @{@link Optional} will only be resolved if the property can be
 * determined.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Optional} may not:
 * <ul>
 * <li>be not annotated with either @{@link Inject} or {@link Property}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(OptionalValidator.class)
public @interface Optional {

	/**
	 * Mode that specifies whether or not to leave a {@link Field}/{@link Parameter}
	 * annotated with @{@link Inject} null depending on existing allocations.
	 * <p>
	 * How the mode is applied depends on the whether the injected bean is
	 * independent ({@link Blueprint.TypeAllocation}s) or a {@link Blueprint.SingletonAllocation} (allocation on
	 * the qualifier).
	 * <p>
	 * Note that if a {@link Blueprint.SingletonAllocation} is created in a parent injection sequence,
	 * it is treated as it was allocated in all sub injection sequences, regardless
	 * whether it was explicitly allocated or created on demand.
	 */
	enum InjectionMode {

		/**
		 * Always fill the {@link Field}/{@link Parameter} annotated
		 * with @{@link Inject}; if that is not possible for some reason, throw an
		 * {@link InjectionException}.
		 * <p>
		 * For independent beans:
		 * <ul>
		 * <li>If the type of the injection target is allocated by a
		 * {@link Blueprint.TypeAllocation}, the injection is done using the allocation.</li>
		 * <li>If the type of the injection target is <b>not</b> allocated by a
		 * {@link Blueprint.TypeAllocation}, it is tried to instantiate the type on demand; if
		 * that is not possible, an {@link InjectionException} is thrown</li>
		 * </ul>
		 * <p>
		 * For {@link Blueprint.SingletonAllocation} beans:
		 * <ul>
		 * <li>If the qualifier of the injection target is allocated by a
		 * {@link Blueprint.SingletonAllocation}, the injection is done using the allocation.</li>
		 * <li>If the qualifier of the injection target is <b>not</b> allocated by a
		 * {@link Blueprint.SingletonAllocation}, it is checked whether there already is a singleton for the
		 * qualifier that has been instantiated on demand:
		 * <ul>
		 * <li>If there is none, it is tried to instantiate the type on demand; if that
		 * is not possible, an {@link InjectionException} is thrown</li>
		 * <li>If there is one, it is checked that the type of the instance is exactly
		 * the type of the injection target; if the type is not the same, an
		 * {@link InjectionException} is thrown</li>
		 * </ul>
		 * </li>
		 * </ul>
		 */
		EAGER,

		/**
		 * Leave a {@link Field}/{@link Parameter} annotated with @{@link Inject} null
		 * if its injection is not explicitly anticipated.
		 * <p>
		 * For independent beans:
		 * <ul>
		 * <li>If the type of the injection target is allocated by a
		 * {@link Blueprint.TypeAllocation}, the injection is done using the allocation.</li>
		 * <li>If the type of the injection target is <b>not</b> allocated by a
		 * {@link Blueprint.TypeAllocation}, leave the target null.</li>
		 * </ul>
		 * <p>
		 * For {@link Blueprint.SingletonAllocation} beans:
		 * <ul>
		 * <li>If the qualifier of the injection target is allocated by a
		 * {@link Blueprint.SingletonAllocation}, the injection is done using the allocation.</li>
		 * <li>If the qualifier of the injection target is <b>not</b> allocated by a
		 * {@link Blueprint.SingletonAllocation}, leave the target null.</li>
		 * </ul>
		 */
		EXPLICIT
	}
}
