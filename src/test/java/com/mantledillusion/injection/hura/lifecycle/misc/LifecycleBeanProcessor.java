package com.mantledillusion.injection.hura.lifecycle.misc;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.lifecycle.injectables.AbstractLifecycleInjectable;

public class LifecycleBeanProcessor implements BeanProcessor<AbstractLifecycleInjectable> {

    @Override
    public void process(Phase phase, AbstractLifecycleInjectable bean, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, bean, callback);
    }
}
