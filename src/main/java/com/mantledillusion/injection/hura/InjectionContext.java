package com.mantledillusion.injection.hura;

import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.injection.hura.annotation.Construct;

final class InjectionContext {
	
	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";

	private final Map<String, Object> singletonBeans = new HashMap<>();

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

	<T> void addSingleton(String singletonId, T instance) {
		this.singletonBeans.put(singletonId, instance);
	}

	@SuppressWarnings("unchecked")
	<T> T retrieveSingleton(String singletonId) {
		return (T) this.singletonBeans.get(singletonId);
	}
}