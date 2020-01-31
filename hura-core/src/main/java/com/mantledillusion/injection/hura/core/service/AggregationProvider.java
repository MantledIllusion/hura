package com.mantledillusion.injection.hura.core.service;

import com.mantledillusion.essentials.object.ListEssentials;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiPredicate;

/**
 * Interface for services that provide aggregation of singleton beans.
 */
public interface AggregationProvider extends StatefulService {

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default Collection<Object> aggregate(BiPredicate<String, Object>... predicates) {
        return aggregate(Object.class, predicates);
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default Collection<Object> aggregate(Collection<BiPredicate<String, Object>> predicates) {
        return aggregate(Object.class, predicates);
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param qualifierMatcher The {@link java.util.regex.Pattern} the singleton's qualifier needs to match; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default Collection<Object> aggregate(String qualifierMatcher, BiPredicate<String, Object>... predicates) {
        return aggregate(Object.class, qualifierMatcher, predicates);
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param qualifierMatcher The {@link java.util.regex.Pattern} the singleton's qualifier needs to match; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default Collection<Object> aggregate(String qualifierMatcher, Collection<BiPredicate<String, Object>> predicates) {
        return aggregate(Object.class, qualifierMatcher, predicates);
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param <T> The singleton's type
     * @param type The type the singleton; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default <T> Collection<T> aggregate(Class<T> type, BiPredicate<String, T>... predicates) {
        return aggregate(type, ListEssentials.asList(predicates));
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param <T> The singleton's type
     * @param type The type the singleton; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    <T> Collection<T> aggregate(Class<T> type, Collection<BiPredicate<String, T>> predicates);

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param <T> The singleton's type
     * @param type The type the singleton; might <b>not</b> be null.
     * @param qualifierMatcher The {@link java.util.regex.Pattern} the singleton's qualifier needs to match; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default <T> Collection<T> aggregate(Class<T> type, String qualifierMatcher, BiPredicate<String, T>... predicates) {
        return aggregate(type, qualifierMatcher, ListEssentials.asList(predicates));
    }

    /**
     * Aggregates all singleton beans matching the {@link java.util.function.Predicate}s.
     *
     * @param <T> The singleton's type
     * @param type The type the singleton; might <b>not</b> be null.
     * @param qualifierMatcher The {@link java.util.regex.Pattern} the singleton's qualifier needs to match; might <b>not</b> be null.
     * @param predicates The {@link BiPredicate}s the singleton and its qualifier need to match; might be null or contain nulls.
     * @return A new {@link Collection} of all singletons matching the {@link java.util.function.Predicate}s, never null, might be empty
     */
    default <T> Collection<T> aggregate(Class<T> type, String qualifierMatcher, Collection<BiPredicate<String, T>> predicates) {
        if (qualifierMatcher == null) {
            throw new IllegalArgumentException("Cannot match a singleton's qualifier against a null matcher");
        }
        predicates = predicates == null ? new ArrayList<>() : new ArrayList<>(predicates);
        predicates.add((qualifier, bean) -> qualifier.matches(qualifierMatcher));
        return aggregate(type, predicates);
    }
}
