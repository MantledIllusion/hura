package com.mantledillusion.injection.hura.annotation.injection;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.instruction.Context;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

/**
 * Extension {@link Annotation} to @{@link Inject}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Inject}
 * and @{@link Global} will be injected with a singleton taken from the
 * {@link SingletonMode#GLOBAL} singleton pool.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Global} may not:
 * <ul>
 * <li>be not annotated with @{@link Inject}</li>
 * <li>be annotated with @{@link Inject} without specifying a qualifier.</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(GlobalValidator.class)
public @interface Global {

	/**
	 * Mode that specifies from which injection context to retrieve a singleton.
	 */
	enum SingletonMode {

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
}
