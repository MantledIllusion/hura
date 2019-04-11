package com.mantledillusion.injection.hura.annotation.lifecycle.annotation;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * Interface for processors on occurrences of a specifiable
 * {@link Annotation} on a miscellaneous {@link AnnotatedElement}.
 * <p>
 * An {@link AnnotationProcessor} might be executed during reflective analysis,
 * when an {@link Annotation} (that itself is annotated with a lifecycle annotation
 * and specifies the {@link AnnotationProcessor} implementation) is found on an
 * {@link AnnotatedElement}.
 *
 * @param <A> The {@link Annotation} type this {@link AnnotationProcessor}
 *            inspects.
 * @param <E> The {@link AnnotatedElement} type the {@link Annotation} this
 *            {@link AnnotationProcessor} processes can be found on. Suitable types depend
 *            on the {@link Target} the {@link Annotation} this
 *            {@link AnnotationProcessor} processes might be annotated on.
 */
public interface AnnotationProcessor<A extends Annotation, E extends AnnotatedElement> {

    /**
     * Validates the given {@link Annotation} occurrence on the given
     * {@link AnnotatedElement}.
     *
     * @param phase              The {@link Phase} this processor is executed during; might <b>not</b> be null.
     * @param bean               The bean as injected at the current {@link Phase}; might be null depending on the {@link Phase}.
     * @param annotationInstance The instance of the {@link Annotation} to process that was found
     *                           on the {@link AnnotatedElement}; might <b>not</b> be null.
     * @param annotatedElement   The {@link AnnotatedElement} the {@link Annotation} to process
     *                           that was found on; might <b>not</b> be null.
     * @param callback           The {@link TemporalInjectorCallback} to the {@link com.mantledillusion.injection.hura.Injector}
     *                           the injection of the bean is executed by; might be null depending on the {@link Phase}.
     * @throws Exception Exceptions that may be thrown during validation.
     */
    void process(Phase phase, Object bean, A annotationInstance, E annotatedElement, TemporalInjectorCallback callback) throws Exception;
}