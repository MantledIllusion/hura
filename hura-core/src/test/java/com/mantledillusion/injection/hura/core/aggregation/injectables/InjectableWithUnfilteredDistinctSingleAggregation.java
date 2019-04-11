package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

public class InjectableWithUnfilteredDistinctSingleAggregation {

    @Aggregate(distinct = true)
    public Object singleton;
}
