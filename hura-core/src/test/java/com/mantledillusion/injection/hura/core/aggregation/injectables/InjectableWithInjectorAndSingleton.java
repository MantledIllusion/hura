package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class InjectableWithInjectorAndSingleton {

    public static final String QUALIFIER = "singleton";

    @Inject
    public Injector injector;

    @Inject
    @Qualifier(QUALIFIER)
    public Injectable singleton;
}
