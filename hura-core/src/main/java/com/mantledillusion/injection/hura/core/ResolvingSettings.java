package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

final class ResolvingSettings<T> {

	final Class<T> targetType;
	final String resolvableValue;
	final String matcher;
	final boolean forced;
	final Map<Resolve.ResolvingHint.HintType, String> hints;
	
	private ResolvingSettings(Class<T> targetType, String resolvableValue, String matcher, boolean forced,
							  Map<Resolve.ResolvingHint.HintType, String> hints) {
		this.targetType = targetType;
		this.resolvableValue = resolvableValue;
		this.matcher = matcher;
		this.forced = forced;
		this.hints = hints;
	}

	static <T> ResolvingSettings<T> of(Class<T> targetType, Resolve property, Matches matches, Optional optional) {
		boolean forced = true;
		String matcher = Matches.DEFAULT_MATCHER;
		if (matches != null) {
			matcher = matches.value();
		}
		if (optional != null) {
			forced = false;
		}
		return new ResolvingSettings<>(targetType, property.value(), matcher, forced, Arrays.stream(property.hints()).
				collect(Collectors.toMap(Resolve.ResolvingHint::type, Resolve.ResolvingHint::value)));
	}

	static ResolvingSettings<String> of(String propertyKey) {
		return new ResolvingSettings<>(String.class, propertyKey, Matches.DEFAULT_MATCHER, true, Collections.emptyMap());
	}

	static <T> ResolvingSettings<T> of(Class<T> targetType, String propertyKey, String matcher, boolean forced,
									   Map<Resolve.ResolvingHint.HintType, String> hints) {
		return new ResolvingSettings<>(targetType, propertyKey, matcher, forced, hints != null ? hints : Collections.emptyMap());
	}
}
