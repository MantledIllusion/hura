package com.mantledillusion.injection.hura.core.exception;

/**
 * Type for {@link RuntimeException}s that occur during {@link com.mantledillusion.injection.hura.core.annotation.property.Resolve}ing
 * when a {@link String} value has to be converted into some other value, but cannot.
 */
public class ConversionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}