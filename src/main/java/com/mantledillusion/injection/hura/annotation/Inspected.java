package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.Processor.Phase;

/**
 * {@link Annotation} for other {@link Annotation}s whose occurrences need to be
 * inspected by an {@link Inspector} during injection.
 */
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Inspected {

	/**
	 * The {@link Inspector} to inject and execute when this @{@link Inspected} is
	 * found during injection.
	 * 
	 * @return The {@link Inspector} to trigger; never null
	 */
	Class<? extends Inspector<?, ?>> value();

	/**
	 * The phase of bean instantiation during which the {@link Inspector} of
	 * this @{@link Inspected} needs to be triggered.
	 * <p>
	 * By default the used {@link Processor.Phase} is {@link Phase#INSPECT}.
	 * 
	 * @return The injection {@link Processor.Phase} in which to trigger the
	 *         {@link Inspector}; never null
	 */
	Phase phase() default Phase.INSPECT;
}
