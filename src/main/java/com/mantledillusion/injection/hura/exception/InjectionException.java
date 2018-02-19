package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.Injector;

/**
 * Type for {@link RuntimeException}s that occur during injection by an {@link Injector}.
 */
public class InjectionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InjectionException(String message) {
		super(message);
	}

	public InjectionException(String message, Throwable t) {
		super(message, t);
	}
}
