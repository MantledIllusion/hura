package com.mantledillusion.injection.hura.annotation.property;

import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

/**
 * Extension {@link Annotation} to @{@link Property}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Property}
 * and @{@link Matches} cause the properties' value to be matched against a
 * matcher.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Matches} may not:
 * <ul>
 * <li>be not annotated with @{@link Property}</li>
 * <li>be annotated with a not matching @{@link DefaultValue}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(MatchesValidator.class)
public @interface Matches {

	String DEFAULT_MATCHER = ".*";

	/**
	 * The {@link Pattern} matcher for the {@link Property} value to match.
	 * 
	 * @return The matcher for the {@link Property}s value; never null, must be
	 *         parsable by {@link Pattern#compile(String)}
	 */
	String value();
}
