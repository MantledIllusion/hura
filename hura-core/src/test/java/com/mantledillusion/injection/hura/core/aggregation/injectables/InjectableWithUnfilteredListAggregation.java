package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

import java.util.List;

public class InjectableWithUnfilteredListAggregation {

    @Aggregate
    public List<Object> singletons;
}
