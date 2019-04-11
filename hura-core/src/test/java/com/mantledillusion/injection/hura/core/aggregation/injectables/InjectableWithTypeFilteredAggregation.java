package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterfaceImpl;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithTypeFilteredAggregation {

    @Aggregate
    public Collection<InjectableInterfaceImpl> singletons;
}
