package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.annotation.Property;

/**
 * Type for {@link RuntimeException}s that occur during {@link Property} resolving.
 */
public class ResolvingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResolvingException(String message) {
		super(message);
	}
}
