package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

import java.util.HashMap;
import java.util.Map;

final class MappingContext {

	static final String MAPPING_CONTEXT_SINGLETON_ID = "_mappingContext";

	private final Map<String, String> mappings = new HashMap<>();

	@Construct
	MappingContext() {
		this(null);
	}

	MappingContext(MappingContext base) {
		if (base != null) {
			this.mappings.putAll(base.mappings);
		}
	}

	boolean hasMapping(String qualifier) {
		return this.mappings.containsKey(qualifier);
	}

	String getMapping(String qualifier) {
		while (this.mappings.containsKey(qualifier)) {
			qualifier = this.mappings.get(qualifier);
		}
		return qualifier;
	}

	MappingContext merge(Map<String, String> qualifierAllocations) {
		MappingContext newContext = new MappingContext(this);
		newContext.mappings.putAll(qualifierAllocations);
		return newContext;
	}
}
