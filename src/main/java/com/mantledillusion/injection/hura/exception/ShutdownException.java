package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.service.StatefulService;

/**
 * Type for {@link RuntimeException}s that occur when calling methods of a {@link StatefulService} after is has been shut down.
 */
public class ShutdownException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ShutdownException(String message) {
		super(message);
	}
}
