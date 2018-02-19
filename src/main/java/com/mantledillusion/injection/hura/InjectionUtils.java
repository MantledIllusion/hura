package com.mantledillusion.injection.hura;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Property;

/**
 * Utilities for injection.
 */
public final class InjectionUtils {

	private InjectionUtils() {
	}

	/**
	 * Checks whether all of the given {@link Executable}'s {@link Parameter}s are
	 * injectable, which means that all of the defined {@link Parameter}s either
	 * have to be annotated with @{@link Inject} or @{@link Property}.
	 * 
	 * @param executable
	 *            The executable to check; might <b>not</b> be null.
	 * @return True if all of the 0-&gt;n {@link Parameter}s are injectable, false
	 *         otherwise
	 */
	public static boolean hasAllParametersDefinable(Executable executable) {
		if (executable == null) {
			throw new IllegalArgumentException("Cannot check parameters of a null executable.");
		}
		for (Parameter parameter : executable.getParameters()) {
			if (!isInjectable(parameter) && !isResolvable(parameter)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the given {@link AnnotatedElement} is annotated
	 * with @{@link Inject}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isInjectable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check injectability of a null annotated element.");
		}
		return e.isAnnotationPresent(Inject.class);
	}

	/**
	 * Checks whether the given {@link AnnotatedElement} is annotated
	 * with @{@link Property}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isResolvable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check resolvability of a null annotated element.");
		}
		return e.isAnnotationPresent(Property.class);
	}
}
