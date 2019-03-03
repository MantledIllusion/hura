package com.mantledillusion.injection.hura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Global.SingletonMode;
import com.mantledillusion.injection.hura.exception.MappingException;

final class MappingContext {

	static final String MAPPING_CONTEXT_SINGLETON_ID = "_mappingContext";

	private final Map<SingletonMode, Map<String, String>> mappings = new HashMap<>();

	@Construct
	MappingContext() {
		this(null);
	}

	MappingContext(MappingContext base) {
		this.mappings.put(SingletonMode.GLOBAL, new HashMap<>());
		this.mappings.put(SingletonMode.SEQUENCE, new HashMap<>());
		if (base != null) {
			this.mappings.get(SingletonMode.GLOBAL).putAll(base.mappings.get(SingletonMode.GLOBAL));
			this.mappings.get(SingletonMode.SEQUENCE).putAll(base.mappings.get(SingletonMode.SEQUENCE));
		}
	}

	boolean hasMapping(String qualifier, SingletonMode mode) {
		return this.mappings.get(mode).containsKey(qualifier);
	}

	String map(String qualifier, SingletonMode mode) {
		while (this.mappings.get(mode).containsKey(qualifier)) {
			qualifier = this.mappings.get(mode).get(qualifier);
		}
		return qualifier;
	}

	void addMapping(String mappingBase, String mappingTarget, SingletonMode mode) {
		List<String> qualifiers = new ArrayList<>();
		qualifiers.add(mappingBase);
		Map<String, String> mappings = new HashMap<>(this.mappings.get(mode));
		mappings.put(mappingBase, mappingTarget);

		String target = mappingTarget;
		while (mappings.containsKey(target)) {
			target = mappings.get(target);
			if (mappingBase.equals(target)) {
				throw new MappingException("qualifier mapping loop detected! Adding a mapping from '" + mappingBase
						+ "' to '" + mappingTarget + "' closes the mapping loop '"
						+ StringUtils.join(qualifiers, "' -> '") + "'");
			}
		}

		this.mappings.get(mode).put(mappingBase, mappingTarget);
	}

	MappingContext merge(Map<SingletonMode, Map<String, String>> qualifierAllocations) {
		MappingContext newContext = new MappingContext(this);
		newContext.mappings.get(SingletonMode.GLOBAL).putAll(qualifierAllocations.get(SingletonMode.GLOBAL));
		newContext.mappings.get(SingletonMode.SEQUENCE).putAll(qualifierAllocations.get(SingletonMode.SEQUENCE));
		return newContext;
	}

	MappingContext merge(MappingContext other) {
		MappingContext newContext = new MappingContext(this);
		newContext.mappings.get(SingletonMode.GLOBAL).putAll(other.mappings.get(SingletonMode.GLOBAL));
		newContext.mappings.get(SingletonMode.SEQUENCE).putAll(other.mappings.get(SingletonMode.SEQUENCE));
		return newContext;
	}
}
