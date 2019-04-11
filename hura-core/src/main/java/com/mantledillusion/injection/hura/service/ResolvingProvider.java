package com.mantledillusion.injection.hura.service;

import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.exception.ResolvingException;
import com.mantledillusion.injection.hura.exception.ShutdownException;

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
     * @throws ShutdownException If the instance has already been shut down
     */
    default String resolve(String propertyKey, boolean forced) throws ShutdownException {
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
     * @throws ShutdownException If the instance has already been shut down
     */
    String resolve(String propertyKey, String matcher, boolean forced) throws ShutdownException;

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
     * @throws ShutdownException If the instance has already been shut down
     */
    default String resolve(String propertyKey, String defaultValue) throws ShutdownException {
        return resolve(propertyKey, Matches.DEFAULT_MATCHER, defaultValue);
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
     * @throws ShutdownException If the instance has already been shut down
     */
    String resolve(String propertyKey, String matcher, String defaultValue) throws ShutdownException;
}
