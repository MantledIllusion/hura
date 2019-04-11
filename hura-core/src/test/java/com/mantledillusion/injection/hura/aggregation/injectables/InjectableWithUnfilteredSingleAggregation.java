package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithUnfilteredSingleAggregation {

    @Aggregate
    public Object singleton;
}
