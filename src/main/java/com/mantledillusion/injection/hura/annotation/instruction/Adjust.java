package com.mantledillusion.injection.hura.annotation.instruction;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.exception.InjectionException;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s whose injection
 * needs to be customized.
 * <p>
 * Note that customization is not allowed for {@link Singleton} injections, as
 * that would allow the injected {@link Singleton}s to be defined differently
 * depending on where they get injected first. For the same reason,
 * {@link Singleton} defining is also not allowed; there is no attribute to
 * define them conveniently and such definitions in the
 * {@link BlueprintTemplate} {@link #extensions()} cause an
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
 * with @{@link Inject} injecting a {@link Singleton}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(AdjustValidator.class)
public @interface Adjust {

	/**
	 * Defines a {@link Property}.
	 */
	@interface PropertyDef {

		/**
		 * Returns the key of the {@link Property}.
		 * 
		 * @return The key; might <b>not</b> be null or empty
		 */
		String key();

		/**
		 * Returns the value of the {@link Property}.
		 * 
		 * @return The value; might <b>not</b> be null
		 */
		String value();
	}

	/**
	 * Defines a {@link Mapping}.
	 */
	@interface MappingDef {

		/**
		 * The qualifier that is mapped. Singleton references to this mapping base ID
		 * will reference the mapping target singleton afterwards.
		 * 
		 * @return The qualifier to map; might <b>not</b> be null.
		 */
		String base();

		/**
		 * The qualifier that is mapped to. Singleton references to the mapping base
		 * ID will reference this mapping target ID's singleton afterwards.
		 * 
		 * @return The qualifier to map to; might <b>not</b> be null.
		 */
		String target();

		/**
		 * The {@link SingletonMode} that determines which pool's {@link Singleton}s
		 * this mapping refers to
		 * 
		 * @return The mode; might <b>not</b> be null, {@link SingletonMode#SEQUENCE} by
		 *         default.
		 */
		SingletonMode mode() default SingletonMode.SEQUENCE;
	}

	/**
	 * Specifies an array of {@link Property}s that should adjust the injection.
	 * <p>
	 * This is a convenience function for not having to implement a
	 * {@link BlueprintTemplate} for the {@link #extensions()} if only a
	 * {@link Property} adjustment is needed.
	 * <p>
	 * Note that if any of the {@link #extensions()} define a {@link Property} of
	 * the same key, the {@link Property} defined here overrides it.
	 * 
	 * @return The {@link Property}s to adjust with; might be null or contain nulls,
	 *         both is ignored
	 */
	PropertyDef[] properties() default {};

	/**
	 * Specifies an array of {@link Mapping}s that should adjust the injection.
	 * <p>
	 * This is a convenience function for not having to implement a
	 * {@link BlueprintTemplate} for the {@link #extensions()} if only a
	 * {@link Mapping} adjustment is needed.
	 * <p>
	 * Note that if any of the {@link #extensions()} define a {@link Mapping} of the
	 * same qualifier/mode pair, the {@link Mapping} defined here overrides it.
	 * 
	 * @return The {@link Mapping}s to adjust with; might be null or contain nulls,
	 *         both is ignored
	 */
	MappingDef[] mappings() default {};

	/**
	 * Specifies an array of {@link BlueprintTemplate} implementations that should
	 * adjust the injection.
	 * <p>
	 * The {@link BlueprintTemplate} implementing {@link Class}es given here will be
	 * instantiated and injected by the {@link Injector} on the fly and then
	 * automatically parsed into {@link Blueprint} instances, which allows injection
	 * to be used in the templates.
	 * 
	 * @return The extensions to adjust with; might be null or contain nulls, both
	 *         is ignored
	 */
	Class<? extends BlueprintTemplate>[] extensions() default {};
}
