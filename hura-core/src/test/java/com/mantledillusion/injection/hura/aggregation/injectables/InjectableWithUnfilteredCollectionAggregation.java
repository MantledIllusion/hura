package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithUnfilteredCollectionAggregation {

    @Aggregate
    public Collection<Object> singletons;
}
