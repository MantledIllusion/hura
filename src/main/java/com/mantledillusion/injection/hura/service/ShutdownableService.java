package com.mantledillusion.injection.hura.service;

import com.mantledillusion.injection.hura.exception.ShutdownException;

import java.lang.reflect.Method;

/**
 * Interface for services that can be shut down, so their instances will no longer accept calls to their methods.
 */
public interface ShutdownableService {

    /**
     * Determines whether this {@link ShutdownableService} has been shut down.
     *
     * @return True if the {@link ShutdownableService} is still active, false otherwise; if false is
     * returned, all calls to any of the instantiate()/resolve()/... {@link Method}s
     * will fail with {@link IllegalStateException}s
     */
    boolean isShutdown();

    /**
     * Checks if {@link #isShutdown()} = true.
     *
     * @throws ShutdownException If {@link #isShutdown()} = true
     */
    default void checkShutdown() throws ShutdownException {
        if (isShutdown()) {
            throw new ShutdownException("The service is already shutdown; it cannot be used anymore.");
        }
    }
}
