package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link String} {@link Field}s and {@link Parameter}s
 * who have to receive a property value when their {@link Class} is instantiated
 * and injected by an {@link Injector}.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Resolve} may
 * not:<br>
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * </ul>
 * <p>
 * Extensions to this {@link Annotation} are:
 * <ul>
 * <li>@{@link Matches}</li>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(ResolveValidator.class)
public @interface Resolve {

	/**
	 * A hint to the resolving converter on how to process the conversion from the {@link String} property to the
	 * target value type.
	 */
	@interface ResolvingHint {

		/**
		 * Hints for the build-in converters.
		 */
		enum HintType {
			/**
			 * {@link Byte#parseByte(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 */
			BYTE_RADIX("10"),
			/**
			 * {@link Short#parseShort(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 */
			SHORT_RADIX("10"),
			/**
			 * {@link Integer#parseInt(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 */
			INTEGER_RADIX("10"),
			/**
			 * {@link Integer#parseUnsignedInt(String, int)}
			 * <p>
			 * A parsable {@link Boolean}.
			 */
			INTEGER_UNSIGNED(Boolean.FALSE.toString()),
			/**
			 * {@link Long#parseLong(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 */
			LONG_RADIX("10"),
			/**
			 * {@link Long#parseUnsignedLong(String, int)}
			 * <p>
			 * A parsable {@link Boolean}.
			 */
			LONG_UNSIGNED(Boolean.FALSE.toString());

			HintType(String defaultValue) {
				this.defaultValue = defaultValue;
			}

			private final String defaultValue;

			public String getDefault() {
				return defaultValue;
			}
		}

		/**
		 * The {@link HintType}, defining which type type of the {@link ResolvingHint}.
		 *
		 * @return The {@link HintType}, never null
		 */
		HintType type();

		/**
		 * The value of the {@link ResolvingHint}, containing a value as required by the converter to pick up the hint.
		 *
		 * @return The value, never null
		 */
		String value();
	}

	/**
	 * The property key to resolve.
	 * <p>
	 * <b>Resolvable Value</b>; properties can be used within it.
	 * 
	 * @return The property key to resolve; never null or empty
	 */
	String value();

	/**
	 * The {@link ResolvingHint}s to pass on to the converter that converts the {@link String} property to the type of
	 * the element annotated with @{@link Resolve}.
	 *
	 * @return The hints, never null, might be empty
	 */
	ResolvingHint[] hints() default {};
}