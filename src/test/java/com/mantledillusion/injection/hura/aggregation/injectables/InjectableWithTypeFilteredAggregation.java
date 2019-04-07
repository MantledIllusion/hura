package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.InjectableInterfaceImpl;
import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithTypeFilteredAggregation {

    @Aggregate
    public Collection<InjectableInterfaceImpl> singletons;
}
