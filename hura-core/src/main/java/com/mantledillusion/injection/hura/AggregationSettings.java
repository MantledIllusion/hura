package com.mantledillusion.injection.hura;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import org.apache.commons.lang3.StringUtils;

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

    private AggregationSettings(Class<T> type, AggregationMode aggregationMode, String qualifierMatcher, Class<? extends BiPredicate<String, T>>[] predicates, boolean distinct, boolean optional) {
        this.type = type;
        this.aggregationMode = aggregationMode;
        this.qualifierMatcher = qualifierMatcher;
        this.predicates = predicates;
        this.distinct = distinct;
        this.optional = optional;
    }

    static <T> AggregationSettings of(Class<T> type, AggregationMode aggregationMode, Aggregate aggregate, Optional optional) {
        return new AggregationSettings(type, aggregationMode, StringUtils.defaultIfEmpty(aggregate.qualifierMatcher(), null), aggregate.predicates(), aggregate.distinct(), optional != null);
    }
}
