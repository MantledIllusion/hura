package com.mantledillusion.injection.hura.core;

import com.mantledillusion.essentials.string.StringEssentials;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;
import com.mantledillusion.injection.hura.core.exception.ResolvingException;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class ResolvingContext {
	
	static final String RESOLVING_CONTEXT_SINGLETON_ID = "_resolvingContext";

	private final Map<String, String> properties = new HashMap<>();
	
	@Construct
	ResolvingContext() {
	}
	
	ResolvingContext(ResolvingContext base) {
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

	String resolve(ResolvingSettings set) {
		String resolved = deepReplace(set);

		String matcher = deepReplace(ResolvingSettings.of(set.matcher, true));
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

		return resolved;
	}

	private String deepReplace(ResolvingSettings set) {
		return StringEssentials.deepReplace(set.resolvableValue, value -> {
			if (hasProperty(value)) {
				return getProperty(value);
			} else if (set.forced) {
				throw new ResolvingException("The property '" + value
						+ "' is not set, but is required to be to resolve the value of '" + set.resolvableValue + "'.");
			}  else {
				return value;
			}
		}, value -> hasProperty(value));
	}
}
