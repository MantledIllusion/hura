package com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Annotation}s that need to be called at the
 * {@link Phase#PRE_DESTROY} phase of a bean's life cycle.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface PreDestroy {

	/**
	 * The {@link AnnotationProcessor} implementations to instantiate and apply on bean
	 * instances of a {@link Class} somewhere annotated with an annotation that itself
	 * is annotated with @{@link PreDestroy}.
	 *
	 * @return The {@link AnnotationProcessor} implementations to inject and
	 *         execute on a bean; never null, might be empty
	 */
	Class<? extends AnnotationProcessor<?, ?>>[] value() default {};
}