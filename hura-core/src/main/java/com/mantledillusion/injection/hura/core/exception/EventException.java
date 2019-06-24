package com.mantledillusion.injection.hura.core.exception;

/**
 * Type for {@link RuntimeException}s that occur during receival of an event.
 */
public class EventException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EventException(String message, Throwable t) {
        super(message, t);
    }
}
