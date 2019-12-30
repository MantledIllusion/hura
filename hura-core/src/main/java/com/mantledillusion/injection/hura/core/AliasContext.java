package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

import java.util.HashMap;
import java.util.Map;

final class AliasContext {

	static final String ALIAS_CONTEXT_SINGLETON_ID = "_aliasContext";

	private final Map<String, String> mappings = new HashMap<>();

	@Construct
	AliasContext() {
		this(null);
	}

	AliasContext(AliasContext base) {
		if (base != null) {
			this.mappings.putAll(base.mappings);
		}
	}

	boolean hasMapping(String qualifier) {
		return this.mappings.containsKey(qualifier);
	}

	String getAlias(String qualifier) {
		while (this.mappings.containsKey(qualifier)) {
			qualifier = this.mappings.get(qualifier);
		}
		return qualifier;
	}

	AliasContext merge(Map<String, String> qualifierAllocations) {
		AliasContext newContext = new AliasContext(this);
		newContext.mappings.putAll(qualifierAllocations);
		return newContext;
	}
}
