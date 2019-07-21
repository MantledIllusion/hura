package com.mantledillusion.injection.hura.core.annotation.injection;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.core.exception.AggregationException;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.function.BiPredicate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Field}s who have to be
 * injected with singleton beans of an {@link Injector}.
 * <p>
 * {@link Field}s annotated with @{@link Aggregate} may not:
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * <li>be also annotated with @{@link Inject}</li>
 * <li>be also annotated with @{@link Plugin}</li>
 * </ul>
 * <p>
 * Extensions to this {@link Annotation} are:
 * <ul>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({FIELD})
@PreConstruct(AggregateValidator.class)
public @interface Aggregate {

    /**
     * If set, causes a {@link BiPredicate} to be added that matches the singleton's qualifiers against the given
     * {@link java.util.regex.Pattern}.
     * <p>
     * <b>Resolvable Value</b>; properties can be used within it.
     *
     * @return The matcher for the singleton's qualifier, never null, ignored if empty
     */
    String qualifierMatcher() default "";

    /**
     * All {@link BiPredicate}s the singleton and its qualifier need to match to be aggregated.
     *
     * @return The {@link BiPredicate}, never null, ignored if empty
     */
    Class<? extends BiPredicate<String, ? extends Object>>[] predicates() default {};

    /**
     * When @{@link Aggregate} is used on a non-{@link java.util.Collection} field, only one singleton is allowed to
     * match all the predicates.
     * <p>
     * If set to true (and multiple singletons match the predicates), a single random singleton of the aggregated ones
     * is set to the field instead of causing an {@link AggregationException}.
     *
     * @return True if a random singleton should be assigned to the field if multiple are found, false otherwise
     */
    boolean distinct() default false;
}
