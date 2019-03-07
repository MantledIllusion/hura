package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostDestroy;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreDestroy;

public class MethodProcessedLifecycleInjectable extends AbstractLifecycleInjectable {

    @PostInject
    @PostConstruct
    @PreDestroy
    @PostDestroy
    private void process(Phase phase, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, this, callback);
    }
}