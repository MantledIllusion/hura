package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;

/**
 * {@link Annotation} for {@link Method}s that need to be called at a specific
 * {@link Processor.Phase} of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link Process} may not:<br>
 * <ul>
 * <li>be static</li>
 * <li>be having one parameter other than of the type
 * {@link TemporalInjectorCallback}</li>
 * <li>be having more than one parameter</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(METHOD)
@Validated(ProcessValidator.class)
public @interface Process {

	/**
	 * The phase of bean instantiation during which the {@link Method} annotated
	 * with this {@link Annotation} needs to be called.
	 * <p>
	 * By default the used {@link Processor.Phase} is {@link Phase#INJECT}.
	 * 
	 * @return The injection {@link Processor.Phase} in which to call the
	 *         {@link Method} annotated with this @{@link Process}; never null
	 */
	Phase value() default Phase.INJECT;
}
