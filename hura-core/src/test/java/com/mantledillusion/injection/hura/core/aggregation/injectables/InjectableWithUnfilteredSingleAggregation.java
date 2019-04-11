package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

public class InjectableWithUnfilteredSingleAggregation {

    @Aggregate
    public Object singleton;
}
