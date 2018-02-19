package com.mantledillusion.injection.hura;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.annotation.Construct;

final class InjectionContext {
	
	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";

	private final Map<String, Object> singletonBeans = new HashMap<>();
	private final Map<String, List<SelfSustaningProcessor>> selfDefined = new HashMap<>();

	@Construct 
	InjectionContext() {
		this.singletonBeans.put(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, this);
	}

	InjectionContext(InjectionContext baseContext) {
		if (baseContext != null) {
			this.singletonBeans.putAll(baseContext.singletonBeans);
		}
		this.singletonBeans.put(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, this);
	}
	
	boolean hasSingleton(String singletonId) {
		return this.singletonBeans.containsKey(singletonId);
	}

	<T> void addSingleton(String singletonId, T instance, List<SelfSustaningProcessor> destroyables) {
		this.singletonBeans.put(singletonId, instance);
		this.selfDefined.put(singletonId, destroyables);
	}

	@SuppressWarnings("unchecked")
	<T> T retrieveSingleton(String singletonId) {
		return (T) this.singletonBeans.get(singletonId);
	}
	
	Set<String> retrieveSelfDefinedSingletonIds() {
		return Collections.unmodifiableSet(new HashSet<>(this.selfDefined.keySet()));
	}
	
	List<SelfSustaningProcessor> retrieveSelfDefinedSingletonDestroyables(String singletonId) {
		return this.selfDefined.get(singletonId);
	}
	
	void removeSelfDefinedSingleton(String singletonId) {
		if (this.selfDefined.containsKey(singletonId)) {
			this.singletonBeans.remove(singletonId);
			this.selfDefined.remove(singletonId);
		}
	}
}