package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

public class InjectableWithUnfilteredDistinctSingleAggregation {

    @Aggregate(distinct = true)
    public Object singleton;
}
