package com.mantledillusion.injection.hura.core;

import com.mantledillusion.data.saman.*;
import com.mantledillusion.data.saman.exception.NoProcessorException;
import com.mantledillusion.data.saman.interfaces.Converter;
import com.mantledillusion.essentials.string.StringEssentials;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve.ResolvingHint.HintType;
import com.mantledillusion.injection.hura.core.exception.ConversionException;
import com.mantledillusion.injection.hura.core.exception.ResolvingException;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class ResolvingContext {

	private static class BooleanConverter implements Converter<String, Boolean> {

		@Override
		public Boolean toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Boolean.parseBoolean(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Boolean", e);
			}
		}
	}

	private static class CharConverter implements Converter<String, Character> {

		@Override
		public Character toTarget(String source, ProcessingDelegate context) throws Exception {
			if (source.length() != 1) {
				throw new ConversionException("Cannot extract the single Character out of the String '" + source +
						"' which is not exactly 1 character long.");
			}
			return source.charAt(0);
		}
	}

	private static class ByteConverter implements Converter<String, Byte> {

		@Override
		public Byte toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Byte.parseByte(source, Integer.parseInt(context.get(HintType.BYTE_RADIX.name(),
						HintType.BYTE_RADIX.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Byte", e);
			}
		}
	}

	private static class ShortConverter implements Converter<String, Short> {

		@Override
		public Short toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Short.parseShort(source, Integer.parseInt(context.get(HintType.SHORT_RADIX.name(),
						HintType.SHORT_RADIX.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Short", e);
			}
		}
	}

	private static class IntegerConverter implements Converter<String, Integer> {

		@Override
		public Integer toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Boolean.parseBoolean(context.get(HintType.INTEGER_UNSIGNED.name(), HintType.INTEGER_UNSIGNED.getDefault())) ?
						Integer.parseUnsignedInt(source, Integer.parseInt(context.get(HintType.INTEGER_RADIX.name(),
								HintType.INTEGER_RADIX.getDefault()))) :
						Integer.parseInt(source, Integer.parseInt(context.get(HintType.INTEGER_RADIX.name(),
								HintType.INTEGER_RADIX.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Integer", e);
			}
		}
	}

	private static class LongConverter implements Converter<String, Long> {

		@Override
		public Long toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Boolean.parseBoolean(context.get(HintType.LONG_UNSIGNED.name(), HintType.LONG_UNSIGNED.getDefault())) ?
						Long.parseUnsignedLong(source, Integer.parseInt(context.get(HintType.LONG_RADIX.name(),
								HintType.LONG_RADIX.getDefault()))) :
						Long.parseLong(source, Integer.parseInt(context.get(HintType.LONG_RADIX.name(),
								HintType.LONG_RADIX.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Long", e);
			}
		}
	}

	private static class FloatConverter implements Converter<String, Float> {

		@Override
		public Float toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Float.parseFloat(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Float", e);
			}
		}
	}

	private static class DoubleConverter implements Converter<String, Double> {

		@Override
		public Double toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Double.parseDouble(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Double", e);
			}
		}
	}

	private static class BigIntegerConverter implements Converter<String, BigInteger> {

		@Override
		public BigInteger toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return new BigInteger(source, Integer.parseInt(context.get(HintType.BIG_INTEGER_RADIX.name(),
						HintType.BIG_INTEGER_RADIX.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Double", e);
			}
		}
	}

	private static class BigDecimalConverter implements Converter<String, BigDecimal> {

		@Override
		public BigDecimal toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				DecimalFormat format = new DecimalFormat(context.get(HintType.BIG_DECIMAL_FORMAT.name(),
						HintType.BIG_DECIMAL_FORMAT.getDefault()));
				format.setParseBigDecimal(true);
				return (BigDecimal) format.parse(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Double", e);
			}
		}
	}

	private static class LocalDateConverter implements Converter<String, LocalDate> {

		@Override
		public LocalDate toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return LocalDate.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.LOCAL_DATE_FORMAT.name(), HintType.LOCAL_DATE_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to LocalDate", e);
			}
		}
	}

	private static class LocalTimeConverter implements Converter<String, LocalTime> {

		@Override
		public LocalTime toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return LocalTime.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.LOCAL_TIME_FORMAT.name(), HintType.LOCAL_TIME_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to LocalTime", e);
			}
		}
	}

	private static class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

		@Override
		public LocalDateTime toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return LocalDateTime.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.LOCAL_DATE_TIME_FORMAT.name(), HintType.LOCAL_DATE_TIME_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to LocalDateTime", e);
			}
		}
	}

	private static class OffsetTimeConverter implements Converter<String, OffsetTime> {

		@Override
		public OffsetTime toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return OffsetTime.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.OFFSET_TIME_FORMAT.name(), HintType.OFFSET_TIME_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to OffsetTime", e);
			}
		}
	}

	private static class OffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

		@Override
		public OffsetDateTime toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return OffsetDateTime.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.OFFSET_DATE_TIME_FORMAT.name(), HintType.OFFSET_DATE_TIME_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to OffsetDateTime", e);
			}
		}
	}

	private static class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

		@Override
		public ZonedDateTime toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return ZonedDateTime.parse(source, DateTimeFormatter.ofPattern(context.get(
						HintType.ZONED_DATE_TIME_FORMAT.name(), HintType.ZONED_DATE_TIME_FORMAT.getDefault())));
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to ZonedDateTime", e);
			}
		}
	}

	private static class InstantConverter implements Converter<String, Instant> {

		@Override
		public Instant toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Instant.parse(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Instant", e);
			}
		}
	}

	private static class PeriodConverter implements Converter<String, Period> {

		@Override
		public Period toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Period.parse(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Period", e);
			}
		}
	}

	private static class DurationConverter implements Converter<String, Duration> {

		@Override
		public Duration toTarget(String source, ProcessingDelegate context) throws Exception {
			try {
				return Duration.parse(source);
			} catch (Exception e) {
				throw new ConversionException("Cannot convert property to Period", e);
			}
		}
	}

	private static final ProcessingService CONVERTER = new DefaultProcessingService(
			ProcessorRegistry.of(Arrays.asList(new BooleanConverter(), new CharConverter(), new ByteConverter(),
					new ShortConverter(), new IntegerConverter(), new LongConverter(), new FloatConverter(),
					new DoubleConverter(), new BigIntegerConverter(), new BigDecimalConverter(),
					new LocalDateConverter(), new LocalTimeConverter(), new LocalDateTimeConverter(),
					new OffsetTimeConverter(), new OffsetDateTimeConverter(), new ZonedDateTimeConverter(),
					new InstantConverter(), new PeriodConverter(), new DurationConverter()
	)));
	
	static final String RESOLVING_CONTEXT_SINGLETON_ID = "_resolvingContext";

	private final Map<String, String> properties = new HashMap<>();
	
	@Construct
	ResolvingContext() {
	}
	
	private ResolvingContext(ResolvingContext base) {
		this.properties.putAll(base.properties);
	}
	
	boolean hasProperty(String propertyKey) {
		return this.properties.containsKey(propertyKey);
	}
	
	String getProperty(String propertyKey) {
		return this.properties.get(propertyKey);
	}
	
	ResolvingContext merge(Map<String, String> propertyAllocations) {
		ResolvingContext newContext = new ResolvingContext(this);
		newContext.properties.putAll(propertyAllocations);
		return newContext;
	}

	<T> T resolve(ResolvingSettings<T> set) {
		String resolved = deepReplace(set);

		String matcher = deepReplace(ResolvingSettings.of(set.matcher));
		try {
			Pattern.compile(matcher);
		} catch (PatternSyntaxException | NullPointerException e) {
			throw new ValidatorException("The matcher '" + matcher + "' (resolved from '" + set.matcher
					+ "') is no valid pattern.", e);
		}

		if (!resolved.matches(matcher)) {
			throw new ResolvingException("The resolved value '" + resolved + "' of '" + set.resolvableValue
					+ "' does not match the required pattern '" + matcher + "' (resolved from '" + set.matcher + "').");
		}

		ProcessingContext context = ProcessingContext.of();
		set.hints.forEach((type, value) -> context.set(type.name(), value));
		try {
			return CONVERTER.process(resolved, set.targetType, context);
		} catch (NoProcessorException e) {
			throw new ConversionException("The resolved value '" + resolved + "' of '" + set.resolvableValue
					+ "' is not convertible into the target type '" + set.targetType +
					"'; converting into this target type is not supported.", e);
		}
	}

	private String deepReplace(ResolvingSettings<?> set) {
		return StringEssentials.deepReplace(set.resolvableValue, value -> {
			if (hasProperty(value)) {
				return getProperty(value);
			} else if (set.forced) {
				throw new ResolvingException("The property '" + value
						+ "' is not set, but is required to be to resolve the value of '" + set.resolvableValue + "'.");
			}  else {
				return value;
			}
		}, this::hasProperty);
	}
}
