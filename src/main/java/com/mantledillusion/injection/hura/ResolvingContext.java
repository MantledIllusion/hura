package com.mantledillusion.injection.hura;

import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.injection.hura.annotation.Construct;

class ResolvingContext {
	
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
	
	void addProperty(String propertyKey, String propertyValue) {
		this.properties.put(propertyKey, propertyValue);
	}
	
	ResolvingContext merge(Map<String, String> propertyAllocations) {
		ResolvingContext newContext = new ResolvingContext(this);
		newContext.properties.putAll(propertyAllocations);
		return newContext;
	}
	
	ResolvingContext merge(ResolvingContext other) {
		ResolvingContext newContext = new ResolvingContext(this);
		newContext.properties.putAll(other.properties);
		return newContext;
	}
}
