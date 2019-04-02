package com.mantledillusion.injection.hura;

import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.exception.ResolvingException;

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
		if (hasProperty(set.propertyKey)) {
			String property = getProperty(set.propertyKey);
			if (!property.matches(set.matcher)) {
				if (!set.forced && set.defaultValue != null) {
					return set.defaultValue;
				} else {
					throw new ResolvingException("The defined property '" + set.propertyKey + "' is set to the value '"
							+ property + "', which does not match the required pattern '" + set.matcher + "'.");
				}
			}
			return property;
		} else if (set.forced) {
			throw new ResolvingException("The property '" + set.propertyKey + "' is not set, but is required to be.");
		} else if (set.defaultValue != null) {
			return set.defaultValue;
		} else {
			return set.propertyKey;
		}
	}
}
