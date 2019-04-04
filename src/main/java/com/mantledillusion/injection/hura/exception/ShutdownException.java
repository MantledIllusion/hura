package com.mantledillusion.injection.hura.exception;

/**
 * Type for {@link RuntimeException}s that occur when calling methods of a {@link com.mantledillusion.injection.hura.service.ShutdownableService} after is has been shut down.
 */
public class ShutdownException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ShutdownException(String message) {
		super(message);
	}
}
