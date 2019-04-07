package com.mantledillusion.injection.hura.service;

import com.mantledillusion.essentials.object.ListEssentials;
import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.exception.ShutdownException;

import java.util.Collection;
import java.util.Collections;

/**
 * Interface for services that provide instantiation, dependency injection and lifecycle handling of beans.
 */
public interface InjectionProvider extends StatefulService {

    /**
     * Instantiates and injects an instance of the given root type.
     *
     * @param <T>
     *            The bean type.
     * @param clazz
     *            The {@link Class} to instantiate and inject; might <b>not</b> be
     *            null.
     * @return An injected instance of the given {@link Class}; never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default <T> T instantiate(Class<T> clazz) throws ShutdownException {
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
     *            example {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *            or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s;
     *            might be null.
     * @param allocations
     *            More {@link Blueprint.Allocation}s to be used during injection;
     *            might be null or contain nulls.
     * @return An injected instance of the given {@link Class}; never null
     * @throws ShutdownException If the instance has already been shut down
     */
    <T> T instantiate(Class<T> clazz, Blueprint.Allocation allocation, Blueprint.Allocation... allocations) throws ShutdownException;

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
     *            defining bindings such as {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *            or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null.
     * @param blueprint
     *            More {@link Blueprint}s to be used during injection; might be null or contain nulls.
     * @return An injected instance of the given {@link Class}; never null
     * @throws ShutdownException If the instance has already been shut down
     */
    default <T> T instantiate(Class<T> clazz, Blueprint blueprint, Blueprint... blueprints) throws ShutdownException {
        return instantiate(clazz, ListEssentials.toList(blueprints, blueprint));
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
     *            defining bindings such as {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *            or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null or contain nulls.
     * @return An injected instance of the given {@link Class}; never null
     * @throws ShutdownException If the instance has already been shut down
     */
    <T> T instantiate(Class<T> clazz, Collection<Blueprint> blueprints) throws ShutdownException;
}
