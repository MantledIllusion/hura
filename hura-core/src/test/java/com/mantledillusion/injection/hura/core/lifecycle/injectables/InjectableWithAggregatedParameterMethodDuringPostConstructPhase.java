package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.core.lifecycle.misc.Qualifier1to9PostfixPredicate;

import java.util.Set;

public class InjectableWithAggregatedParameterMethodDuringPostConstructPhase {

    @Aggregate(predicates = Qualifier1to9PostfixPredicate.class)
    public Set<InjectableInterface> aggregatedByField;
    public Set<InjectableInterface> aggregatedByMethod;

    @PostConstruct
    public void process(@Aggregate(predicates = Qualifier1to9PostfixPredicate.class) Set<InjectableInterface> aggregated) {
        this.aggregatedByMethod = aggregated;
    }
}
