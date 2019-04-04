package com.mantledillusion.injection.hura;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.property.Property;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for injection.
 */
public final class InjectionUtils {

	private InjectionUtils() {
	}

	/**
	 * Checks whether all of the given {@link Executable}'s {@link Parameter}s are
	 * injectables, which means that all of the defined {@link Parameter}s either
	 * have to be annotated with @{@link Inject} or @{@link Property}.
	 * 
	 * @param executable
	 *            The executable to check; might <b>not</b> be null.
	 * @return True if all of the 0-&gt;n {@link Parameter}s are injectables, false
	 *         otherwise
	 */
	public static boolean hasAllParametersDefinable(Executable executable) {
		if (executable == null) {
			throw new IllegalArgumentException("Cannot check parameters of a null executable.");
		}
		for (Parameter parameter : executable.getParameters()) {
			if (!isInjectable(parameter) && !isResolvable(parameter)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the given {@link AnnotatedElement} is either annotated
	 * with @{@link Inject} or @{@link Plugin}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isInjectable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check injectability of a null annotated element.");
		}
		return e.isAnnotationPresent(Inject.class) || e.isAnnotationPresent(Plugin.class);
	}

	/**
	 * Checks whether the given {@link AnnotatedElement} is annotated
	 * with @{@link Property}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isResolvable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check resolvability of a null annotated element.");
		}
		return e.isAnnotationPresent(Property.class);
	}

	/**
	 * Checks whether the given property key is a valid.
	 *
	 * @param propertyKey The property key to check; might be null.
	 * @throws IllegalArgumentException If the property key is not valid
	 */
	public static void checkKey(String propertyKey) throws IllegalArgumentException {
		if (StringUtils.isEmpty(propertyKey)) {
			throw new IllegalArgumentException("Cannot resolve a property using a null key.");
		}
	}

	/**
	 * Checks whether the given matcher is a valid {@link Pattern} and whether the given default property matches it.
	 *
	 * @param matcher The matcher to check; might be null.
	 * @param defaultProperty The default property to check; might be null, which means there is no default property.
	 * @throws IllegalArgumentException If the matcher is no valid {@link Pattern}
	 * @throws ValidatorException If the given property is not null and does not match the given matcher
	 */
	public static void checkMatcher(String matcher, String defaultProperty) throws IllegalArgumentException, ValidatorException {
		Pattern pattern;
		try {
			pattern = Pattern.compile(matcher);
		} catch (PatternSyntaxException | NullPointerException e) {
			throw new IllegalArgumentException("The matcher  '" + matcher + "' is no valid pattern", e);
		}

		if (defaultProperty != null && !pattern.matcher(defaultProperty).matches()) {
			throw new ValidatorException("The the default value '" + defaultProperty
					+ "' does not match the specified matcher pattern '" + matcher + "'.");
		}
	}
}
