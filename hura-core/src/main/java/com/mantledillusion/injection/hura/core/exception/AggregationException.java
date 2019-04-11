package com.mantledillusion.injection.hura.core.exception;

/**
 * Type for {@link RuntimeException}s that occur during aggregating singletons.
 */
public class AggregationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AggregationException(String message) {
		super(message);
	}

	public AggregationException(String message, Throwable t) {
		super(message, t);
	}
}
