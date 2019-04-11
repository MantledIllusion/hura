package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.aggregation.misc.PropertyDependentPredicate;
import com.mantledillusion.injection.hura.annotation.injection.Aggregate;

import java.util.Collection;

public class InjectableWithPredicateFilteredCollectionAggregation {

    @Aggregate(predicates = PropertyDependentPredicate.class)
    public Collection<Object> singletons;
}