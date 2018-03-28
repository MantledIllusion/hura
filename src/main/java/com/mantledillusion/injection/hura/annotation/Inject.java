package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.exception.InjectionException;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s who have to be
 * injected by an {@link Injector} when their {@link Class} is instantiated and
 * injected by one.
 * <p>
 * {@link Field}s annotated with @{@link Inject} may not:<br>
 * - be static<br>
 * - be final
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@Validated(InjectValidator.class)
public @interface Inject {

	/**
	 * Mode that specifies whether or not to leave a {@link Field}/{@link Parameter}
	 * annotated with @{@link Inject} null in certain cases.
	 */
	public enum InjectionMode {

		/**
		 * Always fill the {@link Field}/{@link Parameter} annotated
		 * with @{@link Inject}; if that is not possible for some reason, throw an
		 * {@link InjectionException}.
		 * <p>
		 * Causes a bean of the annotated {@link Field}/{@link Parameter}'s type to be
		 * automatically created if:<br>
		 * - The @{@link Inject} {@link Annotation} hints that the instance to inject is
		 * independent (no singleton)<br>
		 * - The @{@link Inject} {@link Annotation} hints that the instance to inject is
		 * a singleton, but there is no instance for that singletonId available yet -
		 * There is no ready-to-use bean instance given to a {@link BeanAllocation} for
		 * the annotated {@link Field}/{@link Parameter}'s type
		 */
		EAGER,

		/**
		 * Leave a {@link Field}/{@link Parameter} annotated with @{@link Inject} null
		 * if its injection is not explicitly anticipated.
		 * <p>
		 * Causes the annotated field not to be injected (stay null) if:<br>
		 * - The @{@link Inject} {@link Annotation} hints that the instance to inject is
		 * independent (no singleton) and there is no explicit {@link BeanAllocation}
		 * for the {@link Field}/{@link Parameter}'s type<br>
		 * - The @{@link Inject} {@link Annotation} hints that the instance to inject is
		 * a singleton and there is no explicit {@link BeanAllocation} for the
		 * {@link Field}/{@link Parameter}'s singletonId
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
		 * singletonId.
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
		 * singletonId.
		 * <p>
		 * This mode is permitted for @{@link Context} sensitive types (such as
		 * {@link Injector} itself), since it would allow context sensitive entities to
		 * be taken out of their context.
		 */
		GLOBAL
	}

	/**
	 * The singletonId. Can be used to distinguish different singleton instances of
	 * the same type if needed.
	 * <p>
	 * By default the used singletonId is "", meaning independent (no singleton).
	 * 
	 * @return The singletonId under which the singleton to inject into the
	 *         annotated {@link Field}/{@link Parameter} is registered in its
	 *         injection context; never null, might be blank if no singleton but an
	 *         independent bean is desired
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
	 * Flag that indicates from which injection context to take a singleton, if
	 * {@link Inject#value()} is set to a singletonId (is not blank).
	 * <p>
	 * By default the used {@link SingletonMode} is {@link SingletonMode#SEQUENCE}.
	 * 
	 * @return The singleton mode that determines to injection context to take a
	 *         singleton from; never null
	 */
	SingletonMode singletonMode() default SingletonMode.SEQUENCE;

	/**
	 * Flag that indicates whether to overwrite the value of an annotated
	 * {@link Field} with null if the allocated bean is null.
	 * <p>
	 * A null bean might be resolved if {@link Inject#injectionMode()} is set to
	 * {@link InjectionMode#EXPLICIT} and no bean is available, or if there is a
	 * specific {@link BeanAllocation} to null.
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

	/**
	 * {@link BlueprintTemplate} implementations that dynamically extend the
	 * injection of the annotated {@link Field}/{@link Parameter}.
	 * <p>
	 * The {@link BlueprintTemplate} implementing {@link Class}es given here will be
	 * instantiated and injected by the {@link Injector} on the fly and then
	 * automatically parsed into {@link Blueprint} instances. Those instances
	 * definitions will then be used to dynamically extend the definitions of the
	 * injection running to inject the annotated {@link Field}/{@link Parameter}.
	 * <p>
	 * For example, {@link Class} A could have a {@link Field} of the interface I
	 * annotated with @{@link Inject}, containing the extending
	 * {@link BlueprintTemplate} E. The template E has a {@link String}
	 * {@link Field} annotated with @{@link Property} and a @{@link Define}
	 * {@link Method} named "allocate" of the return type
	 * {@link BeanAllocation}&lt;I&gt;. That "allocate" {@link Method} could then
	 * use the property {@link Field} to dynamically decide whether to allocate the
	 * interface type I with the implementing {@link Class} B or C.
	 * <p>
	 * Definitions declared by the extensions are always overridden by definitions
	 * of the running injection; that means that an extension might define a
	 * property for the key "a.property.key" just in case that the running injection
	 * does not.
	 * <p>
	 * Inside the given extension array, if two extensions make a definition to the
	 * same target, the extension with the higher array index overrides the one ith
	 * the lower index.
	 * 
	 * @return The extensions to the injection of the annotated
	 *         {@link Field}/{@link Parameter}; might be null or contain nulls, both
	 *         is ignored
	 */
	Class<? extends BlueprintTemplate>[] extensions() default {};
}
