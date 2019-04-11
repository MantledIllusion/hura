package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithQualifierFilteredAggregation {

    public static final String QUALIFIER_PREFIX = "singleton";

    @Aggregate(qualifierMatcher = QUALIFIER_PREFIX+".*")
    public Collection<Object> singletons;
}
