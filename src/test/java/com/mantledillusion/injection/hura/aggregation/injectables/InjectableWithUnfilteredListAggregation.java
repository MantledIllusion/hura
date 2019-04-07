package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;
import java.util.List;

public class InjectableWithUnfilteredListAggregation {

    @Aggregate
    public List<Object> singletons;
}
