package com.mantledillusion.injection.hura.exception;

/**
 * Type for {@link RuntimeException}s that occur during annotation validation.
 */
public class ValidatorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidatorException(String message) {
		super(message);
	}

	public ValidatorException(String message, Throwable t) {
		super(message, t);
	}
}
