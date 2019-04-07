package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;
import java.util.Set;

public class InjectableWithUnfilteredSetAggregation {

    @Aggregate
    public Set<Object> singletons;
}
