package com.mantledillusion.injection.hura.weblaunch.exception;

/**
 * Type for {@link RuntimeException}s that occur during server startup or shutdown.
 */
public class WeblaunchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WeblaunchException(String message, Throwable t) {
		super(message, t);
	}
}
