package com.mantledillusion.injection.hura.core.service;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.injection.hura.core.exception.ResolvingException;
import com.mantledillusion.injection.hura.core.exception.ShutdownException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Interface for services that provide property resolving.
 */
public interface ResolvingProvider extends StatefulService {

    /**
     * Resolves the given property key.
     * <p>
     * Resolving is not forced.
     *
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default String resolve(String propertyKey) throws ShutdownException {
        return resolve(String.class, propertyKey, Matches.DEFAULT_MATCHER, false);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving is not forced.
     *
     * @param <T>
     *            The target type.
     * @param targetType
     *            The {@link Class} type to convert the resolved {@link String} property to; might <b>not</b> be null.
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default <T> T resolve(Class<T> targetType, String propertyKey) throws ShutdownException {
        return resolve(targetType, propertyKey, Matches.DEFAULT_MATCHER, false);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving might be forced if desired.
     *
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @param forced
     *            Determines whether the resolving has to be successful. If set to true, a {@link ResolvingException}
     *            will be thrown if the key cannot be resolved.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default String resolve(String propertyKey, boolean forced) throws ShutdownException {
        return resolve(String.class, propertyKey, Matches.DEFAULT_MATCHER, forced);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving might be forced if desired.
     *
     * @param <T>
     *            The target type.
     * @param targetType
     *            The {@link Class} type to convert the resolved {@link String} property to; might <b>not</b> be null.
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @param forced
     *            Determines whether the resolving has to be successful. If set to true, a {@link ResolvingException}
     *            will be thrown if the key cannot be resolved.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default <T> T resolve(Class<T> targetType, String propertyKey, boolean forced) throws ShutdownException {
        return resolve(targetType, propertyKey, Matches.DEFAULT_MATCHER, forced);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving might be forced if desired.
     *
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @param matcher
     *            The matcher for the property value; might <b>not</b> be null, must be parsable by
     *            {@link Pattern#compile(String)}.
     * @param forced
     *            Determines whether the resolving has to be successful. If set to true, a {@link ResolvingException}
     *            will be thrown if the key cannot be resolved.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default String resolve(String propertyKey, String matcher, boolean forced) throws ShutdownException {
        return resolve(String.class, propertyKey, matcher, forced);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving might be forced if desired.
     *
     * @param <T>
     *            The target type.
     * @param targetType
     *            The {@link Class} type to convert the resolved {@link String} property to; might <b>not</b> be null.
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @param matcher
     *            The matcher for the property value; might <b>not</b> be null, must be parsable by
     *            {@link Pattern#compile(String)}.
     * @param forced
     *            Determines whether the resolving has to be successful. If set to true, a {@link ResolvingException}
     *            will be thrown if the key cannot be resolved.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default <T> T resolve(Class<T> targetType, String propertyKey, String matcher, boolean forced) throws ShutdownException {
        return resolve(targetType, propertyKey, matcher, forced, null);
    }

    /**
     * Resolves the given property key.
     * <p>
     * Resolving might be forced if desired.
     *
     * @param <T>
     *            The target type.
     * @param targetType
     *            The {@link Class} type to convert the resolved {@link String} property to; might <b>not</b> be null.
     * @param propertyKey
     *            The key to resolve; might <b>not</b> be null or empty.
     * @param matcher
     *            The matcher for the property value; might <b>not</b> be null, must be parsable by
     *            {@link Pattern#compile(String)}.
     * @param forced
     *            Determines whether the resolving has to be successful. If set to true, a {@link ResolvingException}
     *            will be thrown if the key cannot be resolved.
     * @param hints
     *            The {@link Resolve.ResolvingHint}s for the converter to use; might be null.
     * @return The property value, never null
     * @throws ShutdownException If the instance has already been shut down
     */
    <T> T resolve(Class<T> targetType, String propertyKey, String matcher, boolean forced,
                  Map<Resolve.ResolvingHint.HintType, String> hints) throws ShutdownException;
}
