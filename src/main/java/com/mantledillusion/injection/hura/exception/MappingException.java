package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.Predefinable.Singleton;

/**
 * Type for {@link RuntimeException}s that occur during {@link Singleton} ID mapping.
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MappingException(String message) {
		super(message);
	}

	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}
}
