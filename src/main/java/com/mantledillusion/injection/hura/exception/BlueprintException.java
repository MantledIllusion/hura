package com.mantledillusion.injection.hura.exception;

/**
 * Type for {@link RuntimeException}s that occur during {@link com.mantledillusion.injection.hura.Blueprint} building.
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
