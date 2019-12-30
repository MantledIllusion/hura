package com.mantledillusion.injection.hura.core.exception;

import com.mantledillusion.injection.hura.core.Blueprint.SingletonAllocation;

/**
 * Type for {@link RuntimeException}s that occur during {@link SingletonAllocation} qualifier mapping.
 */
public class AliasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AliasException(String message) {
		super(message);
	}
}
