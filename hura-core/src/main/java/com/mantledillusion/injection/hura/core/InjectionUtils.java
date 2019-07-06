package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Utilities for injection.
 */
public final class InjectionUtils {

	private InjectionUtils() {
	}

	/**
	 * Checks whether all of the given {@link Executable}'s {@link Parameter}s are
	 * injectables, which means that all of the defined {@link Parameter}s either
	 * have to be annotated with @{@link Inject} or @{@link Resolve}.
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
	 * with @{@link Resolve}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isResolvable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check resolvability of a null annotated element.");
		}
		return e.isAnnotationPresent(Resolve.class);
	}

	/**
	 * Checks whether the given {@link AnnotatedElement} is annotated
	 * with @{@link Aggregate}.
	 *
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isAggregateable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check aggregateability of a null annotated element.");
		}
		return e.isAnnotationPresent(Aggregate.class);
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

	/**
	 * Finds the element type of the given generic {@link Collection }type.
	 *
	 * @param genericType The {@link Collection} type; might <b>not</b> be null.
	 * @return The element type, never null, {@link Object} if raw or generic type
	 */
	public static Class<?> findCollectionType(Type genericType) {
		Map<TypeVariable<?>, Type> collectionGenericType =
				TypeUtils.getTypeArguments(genericType, Collection.class);
		if (collectionGenericType.isEmpty()) {
			return Object.class;
		}
		Type collectionType = TypeUtils.parameterize(Collection.class, collectionGenericType)
				.getActualTypeArguments()[0];
		return collectionType instanceof Class ? (Class<?>) collectionType : Object.class;
	}

	/**
	 * Compares the given versions.
	 * <p>
	 * The version arrays might have a different length; in this case the shorter one is appended with 0 until it
	 * matches the length of the longer one.
	 *
	 * @param v1 The first version to compare; might <b>not</b> be null.
	 * @param v2 The second version to compare; might <b>not</b> be null.
	 * @return 1 of the first version is higher, 2 if the second one is, 0 if both are equal
	 */
	public static int compareVersions(int[] v1, int[] v2) {
        v1 = Arrays.copyOf(v1, Math.max(v1.length, v2.length));
        v2 = Arrays.copyOf(v2, Math.max(v1.length, v2.length));
        for (int i=0; i<Math.min(v1.length, v2.length); i++) {
            if (v1[i] > v2[i]) {
                return 1;
            } else if (v1[i] < v2[i]) {
                return -1;
            }
        }
        return 0;
    }

	/**
	 * Splits a given {@link String} version (like '1.0.3') into its int parts.
	 *
	 * @param version The versin to parse; might <b>not</b> be null.
	 * @return The parsed version, never null
	 */
	public static int[] parseVersion(String version) {
		String[] split = version.split("\\.");
		int[] v = new int[split.length];
		for (int i=0; i<split.length; i++) {
			v[i] = Integer.parseInt(split[i]);
		}
		return v;

	}
}
