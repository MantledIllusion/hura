package com.mantledillusion.injection.hura;

import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.annotation.Processed;

/**
 * Interface for processors for a specifiable bean type to process at some point
 * of its life cycle.
 * <p>
 * This interface is not needed for {@link Method}-based processing
 * using @{@link Process}; it can be used as additional processor in a
 * {@link PhasedProcessor} when instantiating via {@link BeanAllocation} or in
 * {@link Class}-based processing using @{@link Processed}.
 *
 * @param <T>
 *            The {@link Class} type of the bean to process.
 */
public interface Processor<T> {

	/**
	 * Describes phases of the instantiation of a bean for an injection.
	 */
	public static enum Phase {

		/**
		 * The phase right after the instantiation of the bean;
		 * {@link AnnotationValidator}s have already been running on the beans
		 * {@link Class} at this point.
		 * <p>
		 * The bean is inspected at this point, in a sense that it can now be
		 * technically analyzed.
		 * <p>
		 * For example, the bean could be scanned using reflection; {@link Inspector}s
		 * are executed at this {@link Phase} by default.
		 */
		INSPECT,

		/**
		 * The phase after {@link #INSPECT}; also all {@link Property}s have been into
		 * the bean parsed at this point.
		 * <p>
		 * The bean is constructed at this point, in a sense that it can now be
		 * functionally self processed.
		 * <p>
		 * For example, beans implementing a specific interface could be registered at a
		 * registry or similar semantic operations.
		 */
		CONSTRUCT,

		/**
		 * The phase after {@link #CONSTRUCT}; also all sub beans have been injected
		 * into the bean at this point.
		 * <p>
		 * The bean is fully initialized at this point, in a sense that it can now be
		 * processed in relation to its sub beans.
		 * <p>
		 * For example, an injected service sub bean of the processed bean could be
		 * called to deliver data that is used to initialize the bean;
		 * {@link Processor}s are run at this {@link Phase} by default.
		 */
		INJECT,

		/**
		 * The phase after {@link #INJECT}; also all parent beans have been injected at
		 * this point.
		 * <p>
		 * The bean is finalized at this point, in a sense that it can now be
		 * ready-for-operation processed in relation to its whole bean environment.
		 * <p>
		 * For example, the processed bean could use an event bus to communicate with
		 * any other bean of its injection sequence, knowing that those beans have
		 * already been initialized as well.
		 */
		FINALIZE,

		/**
		 * The phase at the processed bean's end-of-life.
		 * <p>
		 * The bean is deconstructed at this point, in a sense that it can now be
		 * ready-for-garbage-collection processed.
		 * <p>
		 * For example, the processed bean should call all other {@link Object}s
		 * referencing the bean to remove that reference, so garbage collection is able
		 * to collect the bean.
		 */
		DESTROY
	}

	/**
	 * Has to process the given bean.
	 * 
	 * @param bean
	 *            The bean to process; might <b>not</b> be null.
	 * @param callback
	 *            A callback to the injection sequence that caused the call of this
	 *            {@link Processor}. Can be used to instantiate more beans in the
	 *            same sequence; might be null if the {@link Processor} is executed
	 *            in a {@link Phase} that is outside the injection sequence, like
	 *            {@link Phase#FINALIZE} or {@link Phase#DESTROY}.
	 * @throws Exception
	 *             Exceptions that may be thrown during processing.
	 */
	public void process(T bean, TemporalInjectorCallback callback) throws Exception;
}