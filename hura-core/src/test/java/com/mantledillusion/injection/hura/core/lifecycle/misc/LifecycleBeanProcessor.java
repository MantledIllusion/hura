package com.mantledillusion.injection.hura.core.lifecycle.misc;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.core.lifecycle.injectables.AbstractLifecycleInjectable;

public class LifecycleBeanProcessor implements BeanProcessor<AbstractLifecycleInjectable> {

    @Override
    public void process(Phase phase, AbstractLifecycleInjectable bean, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, bean, callback);
    }
}
