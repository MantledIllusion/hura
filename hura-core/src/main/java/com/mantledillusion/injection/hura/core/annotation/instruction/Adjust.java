package com.mantledillusion.injection.hura.core.annotation.instruction;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Blueprint.MappingAllocation;
import com.mantledillusion.injection.hura.core.Blueprint.PropertyAllocation;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.core.exception.InjectionException;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s whose injection
 * needs to be customized.
 * <p>
 * Note that customization is not allowed for {@link Blueprint.SingletonAllocation} injections, as
 * that would allow the injected {@link Blueprint.SingletonAllocation}s to be defined differently
 * depending on where they get injected first. For the same reason,
 * {@link Blueprint.SingletonAllocation} defining is also not allowed; there is no attribute to
 * define them conveniently and such definitions in the
 * {@link Blueprint} {@link #extensions()} cause an
 * {@link InjectionException}.
 * <p>
 * Definitions declared by any of the adjustments are always overridden by the
 * definitions of the running injection, which makes it possible to create an
 * adjustment defining for example a property, just in case the running
 * injection to adjust does not.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Adjust} may not:
 * <ul>
 * <li>Be annotated on a {@link Field}/{@link Parameter} that is not annotated
 * with @{@link Inject}</li>
 * <li>Be annotated on a {@link Field}/{@link Parameter} that is annotated
 * with @{@link Inject} injecting a {@link Blueprint.SingletonAllocation}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(AdjustValidator.class)
public @interface Adjust {

	/**
	 * Defines a {@link PropertyAllocation}.
	 */
	@interface PropertyDef {

		/**
		 * Returns the key of the {@link PropertyAllocation}.
		 * 
		 * @return The key; might <b>not</b> be null or empty
		 */
		String key();

		/**
		 * Returns the value of the {@link PropertyAllocation}.
		 * 
		 * @return The value; might <b>not</b> be null
		 */
		String value();
	}

	/**
	 * Defines a {@link MappingAllocation}.
	 */
	@interface MappingDef {

		/**
		 * The qualifier that is mapped. SingletonAllocation references to this mapping base ID
		 * will reference the mapping target singleton afterwards.
		 * 
		 * @return The qualifier to map; might <b>not</b> be null.
		 */
		String base();

		/**
		 * The qualifier that is mapped to. SingletonAllocation references to the mapping base
		 * ID will reference this mapping target ID's singleton afterwards.
		 * 
		 * @return The qualifier to map to; might <b>not</b> be null.
		 */
		String target();
	}

	/**
	 * Specifies an array of {@link PropertyAllocation}s that should adjust the injection.
	 * <p>
	 * This is a convenience function for not having to implement a
	 * {@link Blueprint} for the {@link #extensions()} if only a
	 * {@link PropertyAllocation} adjustment is needed.
	 * <p>
	 * Note that if any of the {@link #extensions()} define a {@link PropertyAllocation} of
	 * the same key, the {@link PropertyAllocation} defined here overrides it.
	 * 
	 * @return The {@link PropertyAllocation}s to adjust with; might be null or contain nulls,
	 *         both is ignored
	 */
	PropertyDef[] properties() default {};

	/**
	 * Specifies an array of {@link Blueprint.MappingAllocation}s that should adjust the injection.
	 * <p>
	 * This is a convenience function for not having to implement a
	 * {@link Blueprint} for the {@link #extensions()} if only a
	 * {@link MappingAllocation} adjustment is needed.
	 * <p>
	 * Note that if any of the {@link #extensions()} define a {@link MappingAllocation} of the
	 * same qualifier/mode pair, the {@link MappingAllocation} defined here overrides it.
	 * 
	 * @return The {@link Blueprint.MappingAllocation}s to adjust with; might be null or contain nulls,
	 *         both is ignored
	 */
	MappingDef[] mappings() default {};

	/**
	 * Specifies an array of {@link Blueprint} implementations that should
	 * adjust the injection.
	 * <p>
	 * The {@link Blueprint} implementing {@link Class}es given here will be
	 * instantiated and injected by the {@link Injector} on the fly, which
	 * allows injection to be used in the templates.
	 * 
	 * @return The extensions to adjust with; might be null or contain nulls, both
	 *         is ignored
	 */
	Class<? extends Blueprint>[] extensions() default {};
}
