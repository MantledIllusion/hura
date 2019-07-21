package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Extension {@link Annotation} to @{@link Resolve}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Resolve}
 * and @{@link Matches} cause the properties' value to be matched against a
 * matcher.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Matches} may not:
 * <ul>
 * <li>be not annotated with @{@link Resolve}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(MatchesValidator.class)
public @interface Matches {

	String DEFAULT_MATCHER = ".*";

	/**
	 * The {@link Pattern} matcher for the {@link Resolve} value to match.
	 * <p>
	 * <b>Resolvable Value</b>; properties can be used within it.
	 * 
	 * @return The matcher for the {@link Resolve}s value; never null, must be
	 *         parsable by {@link Pattern#compile(String)}
	 */
	String value();
}
