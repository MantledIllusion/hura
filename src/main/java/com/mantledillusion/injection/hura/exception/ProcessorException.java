package com.mantledillusion.injection.hura.exception;

import com.mantledillusion.injection.hura.Processor;

/**
 * Type for {@link RuntimeException}s that occur during executing
 * {@link Processor}s.
 */
public class ProcessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessorException(String message, Throwable t) {
		super(message, t);
	}
}
