package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.Processor.Phase;

/**
 * {@link Annotation} for {@link Class}es that need specific {@link Processor}
 * implementations to be applied at a specific phase during bean instantiation
 * so they can process the bean for its use.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Processed {

	/**
	 * Defines a single {@link Processor} implementation that needs to be executed
	 * at a specific {@link Processor.Phase} in the life cycle of a bean of the {@link Class}
	 * annotated with @{@link Processed}.
	 */
	@interface PhasedProcessor {

		/**
		 * The {@link Processor} implementation to instantiate and apply on bean
		 * instances of a {@link Class} annotated with @{@link Processed}.
		 * 
		 * @return The {@link Processor} implementation to instantiate, inject and
		 *         execute on a bean of the {@link Class} annotated with
		 *         {@link Processed}; never null
		 */
		Class<? extends Processor<?>> value();

		/**
		 * The phase of bean instantiation during which the {@link Processor} needs to
		 * be executed.
		 * <p>
		 * By default the used {@link Processor.Phase} is {@link Phase#INJECT}.
		 * 
		 * @return The injection {@link Processor.Phase} in which to apply the {@link Processor}
		 *         on a bean of the {@link Class} annotated with {@link Processed};
		 *         never null
		 */
		Phase phase() default Phase.INJECT;
	}

	/**
	 * The {@link PhasedProcessor}s to execute on bean instances of the
	 * {@link Class} this @{@link Processed} annotates.
	 * 
	 * @return The {@link PhasedProcessor}s to execute; never null, might be empty
	 */
	PhasedProcessor[] value();
}
