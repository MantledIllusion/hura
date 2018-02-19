package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;

/**
 * Type for {@link RuntimeException}s that occur during {@link TypedBlueprint} building.
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
