package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;

/**
 * Interface for processors for a specifiable bean type to process at some point
 * of its life cycle.
 * <p>
 * This interface is not needed for {@link Method}-based processing
 * using lifecycle annotations; it can be used as additional processor when
 * instantiating via {@link BeanAllocation} or in @link Class}-based processing
 * using lifecycle annotations.
 *
 * @param <T>
 *            The {@link Class} type of the bean to process.
 */
public interface BeanProcessor<T> {

	/**
	 * Has to process the given bean.
	 *
	 * @param phase The {@link Phase} this processor is executed during; might <b>not</b> be null.
	 * @param bean
	 *            The bean to process; might <b>not</b> be null.
	 * @param callback
	 *            AE callback to the injection sequence that caused the call of this
	 *            {@link BeanProcessor}. Can be used to instantiate more beans in the
	 *            same sequence; might be null if the {@link BeanProcessor} is executed
	 *            in a phase at the end of the bean's lifecycle.
	 * @throws Exception
	 *             Exceptions that may be thrown during processing.
	 */
	void process(Phase phase, T bean, TemporalInjectorCallback callback) throws Exception;
}