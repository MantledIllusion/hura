package com.mantledillusion.injection.hura.annotation.lifecycle.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;

/**
 * {@link Annotation} for {@link Annotation}s that need to be called at the
 * {@link Phase#PRE_CONSTRUCT} phase of a bean's life cycle.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface PreConstruct {

	/**
	 * The {@link AnnotationProcessor} implementations to instantiate and apply on bean
	 * instances of a {@link Class} somewhere annotated with an annotation that itself
	 * is annotated with @{@link PreConstruct}.
	 *
	 * @return The {@link AnnotationProcessor} implementations to inject and
	 *         execute on a bean; never null, might be empty
	 */
	Class<? extends AnnotationProcessor<?, ?>>[] value() default {};
}
