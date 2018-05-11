package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.exception.InjectionException;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s who have to be
 * injected by an {@link Injector} when their {@link Class} is instantiated and
 * injected by one.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Inject} may not:
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@Validated(InjectValidator.class)
public @interface Inject {

	/**
	 * Mode that specifies whether or not to leave a {@link Field}/{@link Parameter}
	 * annotated with @{@link Inject} null depending on existing allocations.
	 * <p>
	 * How the mode is applied depends on the whether the injected bean is
	 * independent ({@link BeanAllocation}s) or a {@link Singleton} (allocation on
	 * the qualifier).
	 * <p>
	 * Note that if a {@link Singleton} is created in a parent injection sequence,
	 * it is treated as it was allocated in all sub injection sequences, regardless
	 * whether it was explicitly allocated or created on demand.
	 */
	public enum InjectionMode {

		/**
		 * Always fill the {@link Field}/{@link Parameter} annotated
		 * with @{@link Inject}; if that is not possible for some reason, throw an
		 * {@link InjectionException}.
		 * <p>
		 * For independent beans:
		 * <ul>
		 * <li>If the type of the injection target is allocated by a
		 * {@link BeanAllocation}, the injection is done using the allocation.</li>
		 * <li>If the type of the injection target is <b>not</b> allocated by a
		 * {@link BeanAllocation}, it is tried to instantiate the type on demand; if
		 * that is not possible, an {@link InjectionException} is thrown</li>
		 * </ul>
		 * <p>
		 * For {@link Singleton} beans:
		 * <ul>
		 * <li>If the qualifier of the injection target is allocated by a
		 * {@link Singleton}, the injection is done using the allocation.</li>
		 * <li>If the qualifier of the injection target is <b>not</b> allocated by a
		 * {@link Singleton}, it is checked whether there already is a singleton for the
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
		 * {@link BeanAllocation}, the injection is done using the allocation.</li>
		 * <li>If the type of the injection target is <b>not</b> allocated by a
		 * {@link BeanAllocation}, leave the target null.</li>
		 * </ul>
		 * <p>
		 * For {@link Singleton} beans:
		 * <ul>
		 * <li>If the qualifier of the injection target is allocated by a
		 * {@link Singleton}, the injection is done using the allocation.</li>
		 * <li>If the qualifier of the injection target is <b>not</b> allocated by a
		 * {@link Singleton}, leave the target null.</li>
		 * </ul>
		 */
		EXPLICIT
	}

	/**
	 * Mode that specifies from which injection context to retrieve a singleton.
	 */
	public enum SingletonMode {

		/**
		 * Retrieve singletons from (and construct singletons to) the injection context
		 * of the current injection sequence's injection sub tree.
		 * <p>
		 * Using this mode enables multiple different instances of the same
		 * {@link Class} (instantiated by the same {@link Injector} in one injection
		 * sequence each) to each have their own singleton instance while using the same
		 * qualifier.
		 * <p>
		 * In addition, singleton instances of parent injection contexts will be
		 * injected, but singletons introduced by child contexts do not bleed into
		 * parent contexts.
		 */
		SEQUENCE,

		/**
		 * Retrieve singletons from the injection context of the injection tree's root.
		 * <p>
		 * Using this mode enables all beans injected by any {@link Injector} in any
		 * injection sequence of the injection tree to share the same singleton by its
		 * qualifier.
		 * <p>
		 * This mode is permitted for @{@link Context} sensitive types (such as
		 * {@link Injector} itself), since it would allow context sensitive entities to
		 * be taken out of their context.
		 */
		GLOBAL
	}

	/**
	 * The qualifier.
	 * <p>
	 * By default the used qualifier is "", meaning independent (no
	 * {@link Singleton}).
	 * 
	 * @return The qualifier under which the {@link Singleton} to inject into the
	 *         annotated {@link Field}/{@link Parameter} is registered in its
	 *         injection context; never null, might be blank if no {@link Singleton}
	 *         but an independent bean is desired
	 */
	String value() default StringUtils.EMPTY;

	/**
	 * Flag that indicates how to inject the annotated
	 * {@link Field}/{@link Parameter}.
	 * <p>
	 * By default the used {@link InjectionMode} is {@link InjectionMode#EAGER}.
	 * 
	 * @return The injection mode that determines how to inject a
	 *         {@link Field}/{@link Parameter}; never null
	 */
	InjectionMode injectionMode() default InjectionMode.EAGER;

	/**
	 * Flag that indicates from which injection context to take a singleton.
	 * <p>
	 * Is ignored if {@link #value()} is not set.
	 * <p>
	 * By default the used {@link SingletonMode} is {@link SingletonMode#SEQUENCE}.
	 * 
	 * @return The singleton mode that determines to injection context to take a
	 *         singleton from; never null
	 */
	SingletonMode singletonMode() default SingletonMode.SEQUENCE;

	/**
	 * Flag that indicates whether to overwrite the value of an annotated
	 * {@link Field} with null if the resolved bean to inject is null.
	 * <p>
	 * A null bean might be resolved if {@link Inject#injectionMode()} is set to
	 * {@link InjectionMode#EXPLICIT} and no bean is available, or if there is a
	 * specific {@link BeanAllocation}/{@link Singleton} that allocates to null.
	 * <p>
	 * Of course annotated {@link Parameter}s cannot be preset with any value, so
	 * their constructor/method will always be called with null in that
	 * {@link Parameter}s place.
	 * <p>
	 * By default the flag is false, nulls do not overwrite an injected
	 * {@link Field}'s value.
	 * 
	 * @return True if a {@link Field}s value has to be overwritten with null when
	 *         the allocated bean is null; false otherwise
	 */
	boolean overwriteWithNull() default false;
}
