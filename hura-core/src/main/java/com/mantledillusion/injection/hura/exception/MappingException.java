package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.Blueprint.SingletonAllocation;

/**
 * Type for {@link RuntimeException}s that occur during {@link SingletonAllocation} ID mapping.
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MappingException(String message) {
		super(message);
	}
}
