package com.mantledillusion.injection.hura;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mantledillusion.injection.hura.annotation.property.Matches;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.MappingAllocation;
import com.mantledillusion.injection.hura.Blueprint.PropertyAllocation;
import com.mantledillusion.injection.hura.Blueprint.SingletonAllocation;
import com.mantledillusion.injection.hura.exception.ResolvingException;
import com.mantledillusion.injection.hura.exception.ValidatorException;

abstract class InjectionProvider {

	private boolean isShutdown = false;

	/**
	 * Determines whether this {@link InjectionProvider} has been shut down.
	 *
	 * @return True if the {@link InjectionProvider} is still active, false otherwise; if false is
	 * returned, all calls to any of the instantiate()/resolve()/... {@link Method}s
	 * will fail with {@link IllegalStateException}s
	 */
	public boolean isShutdown() {
		return isShutdown;
	}

	protected synchronized void shutdown() {
		this.isShutdown = true;
	}

	protected void checkShutdown() {
		if (this.isShutdown) {
			throw new IllegalStateException(
					"The injection provider is shutdown; it cannot be used for injection anymore.");
		}
	}

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
	 * Instantiates and injects an instance of the given root type.
	 *
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public final <T> T instantiate(Class<T> clazz) {
		return instantiate(clazz, Collections.emptyList());
	}

	/**
	 * Instantiates and injects an instance of the given root type.
	 *
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @param allocation
	 *            The {@link Blueprint.Allocation} to be used during injection, for
	 *            example {@link SingletonAllocation}s, {@link MappingAllocation}s
	 *            or{@link PropertyAllocation}s and {@link Blueprint.TypeAllocation}s;
	 *            might be null.
	 * @param allocations
	 *            More {@link Blueprint.Allocation}s to be used during injection;
	 *            might be null or contain nulls.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public abstract <T> T instantiate(Class<T> clazz, Blueprint.Allocation allocation, Blueprint.Allocation... allocations);

	/**
	 * Instantiates and injects an instance of the given root type.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @param blueprint
	 *            The {@link Blueprint} to be used during injection, for
	 *            defining bindings such as {@link SingletonAllocation}s, {@link MappingAllocation}s
	 *            or{@link PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null.
	 * @param blueprint
	 *            More {@link Blueprint}s to be used during injection; might be null or contain nulls.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public final <T> T instantiate(Class<T> clazz, Blueprint blueprint, Blueprint... blueprints) {
		return instantiate(clazz, InjectionUtils.asList(blueprints, blueprint));
	}

	/**
	 * Instantiates and injects an instance of the given root type.
	 *
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @param blueprints
	 *            {@link Blueprint}s to be used during injection, for
	 *            defining bindings such as {@link SingletonAllocation}s, {@link MappingAllocation}s
	 *            or{@link PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null or contain nulls.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public abstract <T> T instantiate(Class<T> clazz, Collection<Blueprint> blueprints);
}
