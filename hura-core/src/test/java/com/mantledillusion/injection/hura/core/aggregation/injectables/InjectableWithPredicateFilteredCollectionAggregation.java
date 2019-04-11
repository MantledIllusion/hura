package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.aggregation.misc.PropertyDependentPredicate;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithPredicateFilteredCollectionAggregation {

    @Aggregate(predicates = PropertyDependentPredicate.class)
    public Collection<Object> singletons;
}