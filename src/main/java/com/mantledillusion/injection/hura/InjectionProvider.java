package com.mantledillusion.injection.hura;

import java.lang.reflect.Method;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mantledillusion.injection.hura.annotation.property.Matches;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;
import com.mantledillusion.injection.hura.exception.ResolvingException;
import com.mantledillusion.injection.hura.exception.ValidatorException;

abstract class InjectionProvider {
	
	/**
	 * Resolves the given property key.
	 * <p>
	 * Resolving is not forced.
	 * 
	 * @param propertyKey
	 *            The key to resolve; might <b>not</b> be null or empty.
	 * @return The property value, never null
	 */
	public final String resolve(String propertyKey) {
		return resolve(propertyKey, Matches.DEFAULT_MATCHER, false);
	}

	/**
	 * Resolves the given property key.
	 * <p>
	 * Resolving might be forced if desired.
	 * 
	 * @param propertyKey
	 *            The key to resolve; might <b>not</b> be null or empty.
	 * @param forced
	 *            Determines whether the resolving has to be successful. If set to
	 *            true, a {@link ResolvingException} will be thrown if the key
	 *            cannot be resolved.
	 * @return The property value, never null
	 */
	public final String resolve(String propertyKey, boolean forced) {
		return resolve(propertyKey, Matches.DEFAULT_MATCHER, forced);
	}

	/**
	 * Resolves the given property key.
	 * <p>
	 * Resolving might be forced if desired.
	 * 
	 * @param propertyKey
	 *            The key to resolve; might <b>not</b> be null or empty.
	 * @param matcher
	 *            The matcher for the property value; might <b>not</b> be null, must
	 *            be parsable by {@link Pattern#compile(String)}.
	 * @param forced
	 *            Determines whether the resolving has to be successful. If set to
	 *            true, a {@link ResolvingException} will be thrown if the key
	 *            cannot be resolved.
	 * @return The property value, never null
	 */
	public final String resolve(String propertyKey, String matcher, boolean forced) {
		checkKey(propertyKey);
		checkMatcher(matcher, null);

		ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, forced);
		return resolve(set);
	}

	/**
	 * Resolves the given property key.
	 * <p>
	 * Resolving is not forced; if the property cannot be resolved, the given
	 * default value is used.
	 * 
	 * @param propertyKey
	 *            The key to resolve; might <b>not</b> be null or empty.
	 * @param defaultValue
	 *            The default value to return if the key cannot be resolved; might
	 *            <b>not</b> be null.
	 * @return The property value, never null
	 */
	public final String resolve(String propertyKey, String defaultValue) {
		return resolve(propertyKey, Matches.DEFAULT_MATCHER,
				defaultValue);
	}

	/**
	 * Resolves the given property key.
	 * <p>
	 * Resolving is not forced; if the property cannot be resolved or the matcher
	 * fails, the given default value is used.
	 * 
	 * @param propertyKey
	 *            The key to resolve; might <b>not</b> be null or empty.
	 * @param matcher
	 *            The matcher for the property value; might <b>not</b> be null, must
	 *            be parsable by {@link Pattern#compile(String)}.
	 * @param defaultValue
	 *            The default value to return if the key cannot be resolved or the
	 *            value does not match the matcher's pattern; might <b>not</b> be
	 *            null.
	 * @return The property value, never null
	 */
	public final String resolve(String propertyKey, String matcher, String defaultValue) {
		if (defaultValue == null) {
			throw new IllegalArgumentException("Cannot fall back to a null default value.");
		}
		checkKey(propertyKey);
		checkMatcher(matcher, defaultValue);

		ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, defaultValue);
		return resolve(set);
	}
	
	abstract String resolve(ResolvingSettings set);

	private void checkKey(String propertyKey) {
		if (StringUtils.isEmpty(propertyKey)) {
			throw new IllegalArgumentException("Cannot resolve a property using a null key.");
		}
	}

	private void checkMatcher(String matcher, String defaultValue) {
		Pattern pattern;
		try {
			pattern = Pattern.compile(matcher);
		} catch (PatternSyntaxException | NullPointerException e) {
			throw new IllegalArgumentException("The matcher  '" + matcher + "' is no valid pattern", e);
		}

		if (defaultValue != null && !pattern.matcher(defaultValue).matches()) {
			throw new ValidatorException("The the default value '" + defaultValue
					+ "' does not match the specified matcher pattern '" + matcher + "'.");
		}
	}

	/**
	 * Convenience {@link Method} for not having to use
	 * {@link #instantiate(TypedBlueprint)} with the result of
	 * {@link Blueprint#of(Class, Predefinable...)}.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @param predefinables
	 *            The {@link Predefinable}s to be used during injection, such as
	 *            {@link SingletonMode#SEQUENCE} {@link Singleton}s or
	 *            {@link Property}s; might be null or contain nulls, both is
	 *            ignored.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public final <T> T instantiate(Class<T> clazz, Predefinable... predefinables) {
		return instantiate(Blueprint.of(clazz, predefinables));
	}

	/**
	 * Instantiates and injects an instance of the given {@link TypedBlueprint}'s
	 * root type.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param blueprint
	 *            The {@link TypedBlueprint} to use for instantiation and injection;
	 *            might <b>not</b> be null.
	 * @return An injected instance of the given {@link TypedBlueprint}'s root type;
	 *         never null
	 */
	public abstract <T> T instantiate(TypedBlueprint<T> blueprint);
}
