package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLifecycleInjectable {

    public static final String IMPL_PROPERTY_KEY = "_implClass";

    public static final Map<Class<? extends AbstractLifecycleInjectable>, List<Phase>> PHASES = new HashMap<>();

    public static void add(Phase phase, AbstractLifecycleInjectable bean, Injector.TemporalInjectorCallback callback) throws Exception {
        Class<? extends AbstractLifecycleInjectable> impl;
        if (phase == Phase.PRE_CONSTRUCT) {
            impl = (Class<? extends AbstractLifecycleInjectable>)
                    Class.forName(callback.resolve(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY));
        } else {
            impl = bean.getClass();
        }
        AbstractLifecycleInjectable.PHASES.computeIfAbsent(impl, (type -> new ArrayList<>())).add(phase);
    }
}
