package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostDestroy;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;

public class MethodProcessedLifecycleInjectable extends AbstractLifecycleInjectable {

    @PostInject
    private void process(Phase phase, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, this, callback);
    }

    @PostConstruct
    @PreDestroy
    @PostDestroy
    private void process2(Phase phase) throws Exception {
        AbstractLifecycleInjectable.add(phase, this, null);
    }
}