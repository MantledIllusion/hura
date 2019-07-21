package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class InjectableWithResolvedSingleton {

    public static final String PKEY_QUALIFIER = "qualifier";

    @Inject
    @Qualifier("${"+PKEY_QUALIFIER+"}")
    public Injectable singleton;

}
