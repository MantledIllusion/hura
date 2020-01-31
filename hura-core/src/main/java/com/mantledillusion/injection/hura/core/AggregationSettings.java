package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

final class AggregationSettings<T> {

    enum AggregationMode {
        SINGLE,
        LIST,
        SET;
    }

    final Class<T> type;
    final AggregationMode aggregationMode;
    final String qualifierMatcher;
    final Class<? extends BiPredicate<String, T>>[] predicates;
    final boolean distinct;
    final boolean optional;

    private AggregationSettings(Class<T> type, AggregationMode aggregationMode, String qualifierMatcher,
                                Class<? extends BiPredicate<String, T>>[] predicates, boolean distinct, boolean optional) {
        this.type = type;
        this.aggregationMode = aggregationMode;
        this.qualifierMatcher = qualifierMatcher;
        this.predicates = predicates;
        this.distinct = distinct;
        this.optional = optional;
    }

    static <T> AggregationSettings<T> of(Class<T> type, Type genericType, Aggregate aggregate, Optional optional) {
        Class<?> aggregationType;
        AggregationSettings.AggregationMode aggregationMode;
        if (type == Collection.class) {
            aggregationType = InjectionUtils.findCollectionType(genericType);
            aggregationMode = AggregationSettings.AggregationMode.LIST;
        } else if (type == List.class) {
            aggregationType = InjectionUtils.findCollectionType(genericType);
            aggregationMode = AggregationSettings.AggregationMode.LIST;
        } else if (type == Set.class) {
            aggregationType = InjectionUtils.findCollectionType(genericType);
            aggregationMode = AggregationSettings.AggregationMode.SET;
        } else {
            aggregationType = type;
            aggregationMode = AggregationSettings.AggregationMode.SINGLE;
        }
        return new AggregationSettings(aggregationType, aggregationMode,
                StringUtils.defaultIfEmpty(aggregate.qualifierMatcher(), null),
                aggregate.predicates(), aggregate.distinct(), optional != null);
    }
}
