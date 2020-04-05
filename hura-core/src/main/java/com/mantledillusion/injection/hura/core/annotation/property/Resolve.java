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
 * {@link Annotation} for {@link Field}s and {@link Parameter}s that have to receive a property value when their
 * {@link Class} is instantiated and injected by an {@link Injector}.
 * <p>
 * The {@link Field}s and {@link Parameter}s annotated can be of the following types:
 * <ul>
 * <li>{@link String}</li>
 * <li>char / {@link Character}</li>
 * <li>boolean / {@link Boolean}</li>
 * <li>byte / {@link Byte}</li>
 * <li>short / {@link Short}</li>
 * <li>integer / {@link Integer}</li>
 * <li>long / {@link Long}</li>
 * <li>float / {@link Float}</li>
 * <li>double / {@link Double}</li>
 * <li>{@link java.math.BigInteger}</li>
 * <li>{@link java.math.BigDecimal} (None or custom decimal format)</li>
 * <li>{@link java.time.LocalDate} (ISO-8601 or custom date format)</li>
 * <li>{@link java.time.LocalTime} (ISO-8601 or custom time format)</li>
 * <li>{@link java.time.LocalDateTime} (ISO-8601 or custom datetime format)</li>
 * <li>{@link java.time.OffsetTime} (ISO-8601 or custom time format)</li>
 * <li>{@link java.time.OffsetDateTime} (ISO-8601 or custom datetime format)</li>
 * <li>{@link java.time.ZonedDateTime} (ISO-8601 or custom datetime format)</li>
 * <li>{@link java.time.Instant} (ISO-8601 datetime format at UTC)</li>
 * <li>{@link java.time.Period} (ISO-8601 period format)</li>
 * <li>{@link java.time.Duration} (ISO-8601 duration format)</li>
 * </ul>
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Resolve} may not:<br>
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
			 * <p>
			 * 10 by default.
			 */
			BYTE_RADIX("10"),
			/**
			 * {@link Short#parseShort(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 * <p>
			 * 10 by default.
			 */
			SHORT_RADIX("10"),
			/**
			 * {@link Integer#parseInt(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 * <p>
			 * 10 by default.
			 */
			INTEGER_RADIX("10"),
			/**
			 * {@link Integer#parseUnsignedInt(String, int)}
			 * <p>
			 * A parsable {@link Boolean}.
			 * <p>
			 * {@link Boolean#FALSE} by default.
			 */
			INTEGER_UNSIGNED(Boolean.FALSE.toString()),
			/**
			 * {@link Long#parseLong(String, int)}
			 * <p>
			 * A parsable {@link Integer}.
			 * <p>
			 * 10 by default.
			 */
			LONG_RADIX("10"),
			/**
			 * {@link Long#parseUnsignedLong(String, int)}
			 * <p>
			 * A parsable {@link Boolean}.
			 * <p>
			 * {@link Boolean#FALSE} by default.
			 */
			LONG_UNSIGNED(Boolean.FALSE.toString()),
			/**
			 * {@link java.math.BigInteger}
			 * <p>
			 * A parsable {@link Integer}.
			 * <p>
			 * 10 by default.
			 */
			BIG_INTEGER_RADIX("10"),
			/**
			 * {@link java.math.BigInteger}
			 * <p>
			 * A {@link java.text.DecimalFormat}.
			 * <p>
			 * Empty by default.
			 */
			BIG_DECIMAL_FORMAT(""),
			/**
			 * {@link java.time.LocalDate}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern
			 * <p>
			 * ISO local date yyyy-MM-dd (2011-12-03) by default.
			 */
			LOCAL_DATE_FORMAT("yyyy-MM-dd"),
			/**
			 * {@link java.time.LocalTime}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
			 *
			 * ISO local time HH:mm:ss (10:15:30) by default.
			 */
			LOCAL_TIME_FORMAT("HH:mm:ss"),
			/**
			 * {@link java.time.LocalDateTime}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
			 *
			 * ISO local date time yyyy-MM-dd'T'HH:mm:ss (2011-12-03T10:15:30) by default.
			 */
			LOCAL_DATE_TIME_FORMAT("yyyy-MM-dd'T'HH:mm:ss"),
			/**
			 * {@link java.time.OffsetTime}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
			 *
			 * ISO offset time HH:mm:ssXXXXX (10:15:30+01:00) by default.
			 */
			OFFSET_TIME_FORMAT("HH:mm:ssXXXXX"),
			/**
			 * {@link java.time.OffsetDateTime}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
			 *
			 * ISO offset date time yyyy-MM-dd'T'HH:mm:ssXXXXX (2011-12-03T10:15:30+01:00) by default.
			 */
			OFFSET_DATE_TIME_FORMAT("yyyy-MM-dd'T'HH:mm:ssXXXXX"),
			/**
			 * {@link java.time.ZonedDateTime}
			 * <p>
			 * A {@link java.time.format.DateTimeFormatter#ofPattern(String)} pattern.
			 *
			 * ISO zoned date time yyyy-MM-dd'T'HH:mm:ssXXXXX'['VV']' (2011-12-03T10:15:30+01:00[Europe/Paris]) by default.
			 */
			ZONED_DATE_TIME_FORMAT("yyyy-MM-dd'T'HH:mm:ssXXXXX'['VV']'");

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