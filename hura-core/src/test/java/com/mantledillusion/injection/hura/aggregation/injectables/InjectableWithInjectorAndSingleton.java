package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class InjectableWithInjectorAndSingleton {

    public static final String QUALIFIER = "singleton";

    @Inject
    public Injector injector;

    @Inject
    @Qualifier(QUALIFIER)
    public Injectable singleton;
}
