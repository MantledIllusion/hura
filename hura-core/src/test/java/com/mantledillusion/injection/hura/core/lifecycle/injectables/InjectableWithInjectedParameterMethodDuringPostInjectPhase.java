package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.singleton.injectables.InjectableWithSequenceSingleton;

public class InjectableWithInjectedParameterMethodDuringPostInjectPhase {

    @Inject
    @Qualifier(value=InjectableWithSequenceSingleton.SINGLETON)
    public Injectable sequenceSingleton;

    public InjectableWithSequenceSingleton methodInjectedBean;

    @PostInject
    public void process(@Inject InjectableWithSequenceSingleton injectable) {
        this.methodInjectedBean = injectable;
    }
}
