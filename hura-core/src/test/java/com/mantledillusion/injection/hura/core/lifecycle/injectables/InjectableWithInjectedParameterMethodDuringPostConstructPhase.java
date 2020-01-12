package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.core.singleton.injectables.InjectableWithSequenceSingleton;

public class InjectableWithInjectedParameterMethodDuringPostConstructPhase {

    @PostConstruct
    public void process(@Inject InjectableWithSequenceSingleton injectable) {
    }
}
