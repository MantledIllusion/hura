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

/**
 * {@link Annotation} for other {@link Annotation}s whose occurrences need to be
 * validated by an {@link AnnotationProcessor} when the {@link Class} the
 * {@link Annotation} is somewhere used on is analyzed before its first
 * injection.
 * <p>
 * This {@link Annotation} is used to ensure correct use of framework
 * {@link Annotation}s such as {@link Inject}, {@link Construct}, etc, but it
 * can also be used on any other custom {@link Annotation}.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface PreConstruct {

	/**
	 * The {@link AnnotationProcessor} that will be used for {@link Annotation}
	 * occurrence validation.
	 * <p>
	 * The validator's generic {@link Annotation} type should be exactly the type
	 * the {@link Annotation} this @{@link PreConstruct} is annotated on; just as the
	 * {@link AnnotatedElement} type should be set according to the {@link Target}'s
	 * {@link ElementType} the annotated {@link Annotation} can be annotated to.
	 * <p>
	 * Note that the validator will be instantiated, <b>not</b> injected,
	 * since @{@link PreConstruct} might do type based validation only. For per-bean
	 * validation, use lifecycle annotations whose processors are executed during the
	 * injected bean's life time.
	 * 
	 * @return The {@link AnnotationProcessor} implementing type that should be used
	 *         for validation; never null
	 */
	Class<? extends AnnotationProcessor<?, ?>>[] value() default {};
}
