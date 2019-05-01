package com.mantledillusion.injection.hura.core.service;

import com.mantledillusion.injection.hura.core.exception.ShutdownException;

import java.lang.reflect.Method;

/**
 * Interface for services that have a pre active and shut down state, so their instances will not / no longer accept calls to their methods.
 */
public interface StatefulService {

    /**
     * Determines whether this {@link StatefulService} is currently active.
     *
     * @return True if the {@link StatefulService} is still active, false otherwise; if false is
     * returned, all calls to any of the instantiate()/resolve()/aggregate()/... {@link Method}s
     * will fail with {@link IllegalStateException}s
     */
    boolean isActive();

    /**
     * Checks if {@link #isActive()} = true.
     *
     * @throws ShutdownException If {@link #isActive()} = true
     */
    default void checkActive() throws ShutdownException {
        if (!isActive()) {
            throw new ShutdownException("The service is already shutdown; it cannot be used anymore.");
        }
    }
}
