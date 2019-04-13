package com.mantledillusion.injection.hura.boot.exception;

/**
 * Type for {@link RuntimeException}s that occur during server startup or shutdown.
 */
public class BootException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BootException(String message, Throwable t) {
		super(message, t);
	}
}
