package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Class}es and {@link Method}s that need to be called at the
 * {@link Phase#PRE_CONSTRUCT} phase of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link PreConstruct} may not:<br>
 * <ul>
 * <li>be static</li>
 * <li>be having a parameter other than of the types {@link Phase} or
 * {@link TemporalInjectorCallback}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({TYPE})
@com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct(PreConstructValidator.class)
public @interface PreConstruct {

	/**
	 * The {@link BeanProcessor} implementations to inject and apply on bean
	 * instances of a {@link Class} or {@link Method}'s {@link Class} annotated with @{@link PostInject}.
	 *
	 * @return The {@link BeanProcessor} implementation to inject and
	 * execute on a bean; never null
	 */
	Class<? extends BeanProcessor<?>>[] value() default {};
}