package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithPropertyFilteredAggregation {

    public static final String PROPERTY_KEY = "singletonQualifier";

    @Aggregate(qualifierMatcher = "${"+PROPERTY_KEY +"}")
    public Collection<Object> singletons;
}
