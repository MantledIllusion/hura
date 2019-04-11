package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

import java.util.Set;

public class InjectableWithUnfilteredSetAggregation {

    @Aggregate
    public Set<Object> singletons;
}
