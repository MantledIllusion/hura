package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.AnnotationValidator;

/**
 * {@link Annotation} for other {@link Annotation}s whose occurrences need to be
 * validated by an {@link AnnotationValidator} when the {@link Class} the
 * {@link Annotation} is somewhere used on is analyzed before its first
 * injection.
 * <p>
 * This {@link Annotation} is used to ensure correct use of framework
 * {@link Annotation}s such as {@link Inject}, {@link Construct}, etc, but it
 * can also be used on any other custom {@link Annotation}.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Validated {

	/**
	 * The {@link AnnotationValidator} that will be used for {@link Annotation}
	 * occurrence validation.
	 * <p>
	 * The validator's generic {@link Annotation} type should be exactly the type
	 * the {@link Annotation} this @{@link Validated} is annotated on; just as the
	 * {@link AnnotatedElement} type should be set according to the {@link Target}'s
	 * {@link ElementType} the annotated {@link Annotation} can be annotated to.
	 * <p>
	 * Note that the validator will be instantiated, <b>not</b> injected,
	 * since @{@link Validated} might do type based validation only. For per-bean
	 * validation, use {@link Inspected}.
	 * 
	 * @return The {@link AnnotationValidator} implementing type that should be used
	 *         for validation; never null
	 */
	Class<? extends AnnotationValidator<?, ?>> value();
}
