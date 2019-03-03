package com.mantledillusion.injection.hura.annotation.lifecycle.annotation;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.BeanProcessor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Annotation}s, {@link Class}es or {@link Method}s that need to be called at the
 * {@link Phase#POST_CONSTRUCT} phase of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link PostConstruct} may not:<br>
 * <ul>
 * <li>be static</li>
 * <li>be having one parameter other than of the type
 * {@link TemporalInjectorCallback}</li>
 * <li>be having more than one parameter</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE})
public @interface PostConstruct {

	/**
	 * The {@link BeanProcessor} implementations to instantiate and apply on bean
	 * instances of a {@link Class} annotated with @{@link PostConstruct}.
	 *
	 * @return The {@link BeanProcessor} implementation to instantiate, inject and
	 *         execute on a bean of the {@link Class} annotated with
	 *         {@link PostConstruct}; never null
	 */
	Class<? extends AnnotationProcessor<?, ?>>[] value() default {};
}