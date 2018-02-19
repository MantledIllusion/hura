package com.mantledillusion.injection.hura;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.Inspected;

/**
 * Interface for inspectors, which are a special kind of {@link Processor}s that
 * are less interested in processing a specific type of bean than inspecting
 * occurrences of a specifiable {@link Annotation} on a miscellaneous bean.
 * <p>
 * An {@link Inspector} might be called during injection when an
 * {@link Annotation} is found on a bean that itself is annotated with
 * an @{@link Inspected} that specifies that {@link Inspector} to be triggered.
 *
 * @param <A>
 *            The {@link Annotation} type this {@link Inspector} inspects.
 * @param <E>
 *            The {@link AnnotatedElement} type the {@link Annotation} this
 *            {@link Inspector} inspects can be found on.
 */
public interface Inspector<A extends Annotation, E extends AnnotatedElement> {

	/**
	 * Inspects the given {@link Annotation} occurrence on the given
	 * {@link AnnotatedElement} on the given bean.
	 * 
	 * @param bean
	 *            The bean the {@link Annotation} was found on; might <b>not</b> be
	 *            null.
	 * @param annotationInstance
	 *            The instance of the {@link Annotation} to inspect that was found
	 *            on the bean; might <b>not</b> be null.
	 * @param annotatedElement
	 *            The {@link AnnotatedElement} the {@link Annotation} to inspect
	 *            that was found on in the bean; might <b>not</b> be null.
	 * @param callback
	 *            A callback to the injection sequence that caused the call to this
	 *            method. Can be used to instantiate more beans in the same
	 *            sequence; might <b>not</b> be null.
	 * @throws Exception
	 *             Exceptions that may be thrown during processing.
	 */
	public void inspect(Object bean, A annotationInstance, E annotatedElement, TemporalInjectorCallback callback)
			throws Exception;
}