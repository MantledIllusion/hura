package com.mantledillusion.injection.hura.core.exception;

import com.mantledillusion.injection.hura.core.Injector;

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
