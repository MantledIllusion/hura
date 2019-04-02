package com.mantledillusion.injection.hura;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.property.Property;

/**
 * Utilities for injection.
 */
public final class InjectionUtils {

	private InjectionUtils() {
	}

	/**
	 * Checks whether all of the given {@link Executable}'s {@link Parameter}s are
	 * injectables, which means that all of the defined {@link Parameter}s either
	 * have to be annotated with @{@link Inject} or @{@link Property}.
	 * 
	 * @param executable
	 *            The executable to check; might <b>not</b> be null.
	 * @return True if all of the 0-&gt;n {@link Parameter}s are injectables, false
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
	 * Checks whether the given {@link AnnotatedElement} is either annotated
	 * with @{@link Inject} or @{@link Plugin}.
	 * 
	 * @param e
	 *            The {@link AnnotatedElement} to check; might <b>not</b> be null.
	 * @return True if the given element is annotated, false otherwise
	 */
	public static boolean isInjectable(AnnotatedElement e) {
		if (e == null) {
			throw new IllegalArgumentException("Cannot check injectability of a null annotated element.");
		}
		return e.isAnnotationPresent(Inject.class) || e.isAnnotationPresent(Plugin.class);
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

	static <T> List<T> asList(T[] objects, T object) {
		List<T> objectList = objects == null ? new ArrayList<T>() : new ArrayList<>(Arrays.asList(objects));
		objectList.add(object);
		return objectList;
	}
}
