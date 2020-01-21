package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostDestroy;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithResolvedParameterDuringPostDestroyPhase {

    public static final String PROPERTY_KEY = "property.key";

    public String methodResolvedValue;

    @PostDestroy
    public void process(@Resolve("${"+ PROPERTY_KEY+"}") String resolvable) {
        this.methodResolvedValue = resolvable;
    }
}
