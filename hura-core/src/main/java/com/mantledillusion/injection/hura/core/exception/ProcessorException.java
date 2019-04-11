package com.mantledillusion.injection.hura.core.exception;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.BeanProcessor;

/**
 * Type for {@link RuntimeException}s that occur during executing
 * {@link BeanProcessor}s.
 */
public class ProcessorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcessorException(String message) {
		super(message);
	}

	public ProcessorException(String message, Throwable t) {
		super(message, t);
	}
}
