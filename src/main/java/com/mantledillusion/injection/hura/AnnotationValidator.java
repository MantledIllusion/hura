package com.mantledillusion.injection.hura;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.annotation.Validated;

/**
 * Interface for validators that validate occurrences of a specifiable
 * {@link Annotation} on a miscellaneous {@link AnnotatedElement}.
 * <p>
 * An {@link AnnotationValidator} might be executed during reflective analysis,
 * when an {@link Annotation} (that itself is annotated with @{@link Validated}
 * and specifies the {@link AnnotationValidator} implementation) is found on an
 * {@link AnnotatedElement}.
 *
 * @param <A>
 *            The {@link Annotation} type this {@link AnnotationValidator}
 *            inspects.
 * @param <E>
 *            The {@link AnnotatedElement} type the {@link Annotation} this
 *            {@link AnnotationValidator} can be found on. Suitable types depend
 *            on the {@link Target} the {@link Annotation} this
 *            {@link AnnotationValidator} validates might be annotated on.
 */
public interface AnnotationValidator<A extends Annotation, E extends AnnotatedElement> {

	/**
	 * Validates the given {@link Annotation} occurrence on the given
	 * {@link AnnotatedElement}.
	 * 
	 * @param annotationInstance
	 *            The instance of the {@link Annotation} to validate that was found
	 *            on the {@link AnnotatedElement}; might <b>not</b> be null.
	 * @param annotatedElement
	 *            The {@link AnnotatedElement} the {@link Annotation} to validate
	 *            that was found on; might <b>not</b> be null.
	 * @throws Exception
	 *             Exceptions that may be thrown during validation.
	 */
	public void validate(A annotationInstance, E annotatedElement) throws Exception;
}