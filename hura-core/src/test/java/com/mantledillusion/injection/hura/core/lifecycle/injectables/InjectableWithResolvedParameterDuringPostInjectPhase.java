package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithResolvedParameterDuringPostInjectPhase {

    public static final String PROPERTY_KEY = "property.key";

    public String methodResolvedValue;

    @PostInject
    public void process(@Resolve("${"+ PROPERTY_KEY+"}") String resolvable) {
        this.methodResolvedValue = resolvable;
    }
}
