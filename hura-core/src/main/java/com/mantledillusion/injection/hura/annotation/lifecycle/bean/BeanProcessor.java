package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;

/**
 * Interface for processors for a specifiable bean type to process at some point
 * of such a bean instance's life cycle.
 *
 * @param <T> The {@link Class} type of the bean to process.
 */
public interface BeanProcessor<T> {

    /**
     * Has to process the given bean.
     *
     * @param phase    The {@link Phase} this processor is executed during; might <b>not</b> be null.
     * @param bean     The bean as injected at the current {@link Phase}; might be null depending on the {@link Phase}.
     * @param callback The {@link TemporalInjectorCallback} to the {@link com.mantledillusion.injection.hura.Injector}
     *                 the injection of the bean is executed by; might be null depending on the {@link Phase}.
     * @throws Exception Exceptions that may be thrown during processing.
     */
    void process(Phase phase, T bean, TemporalInjectorCallback callback) throws Exception;
}