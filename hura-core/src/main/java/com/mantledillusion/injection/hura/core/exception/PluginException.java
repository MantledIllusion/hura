package com.mantledillusion.injection.hura.core.exception;

/**
 * Type for {@link RuntimeException}s that occur during plugin handling
 */
public class PluginException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PluginException(String message) {
		super(message);
	}

	public PluginException(String message, Exception e) {
		super(message, e);
	}
}