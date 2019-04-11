package com.mantledillusion.injection.hura.core.exception;

import com.mantledillusion.injection.hura.core.Blueprint;

/**
 * Type for {@link RuntimeException}s that occur during {@link Blueprint} building.
 */
public class BlueprintException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BlueprintException(String message) {
		super(message);
	}

	public BlueprintException(String message, Throwable t) {
		super(message, t);
	}
}
