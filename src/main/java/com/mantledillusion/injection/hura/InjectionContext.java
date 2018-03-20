package com.mantledillusion.injection.hura;

import java.util.HashMap;
import java.util.Map;

import com.mantledillusion.injection.hura.annotation.Construct;

class InjectionContext {
	
	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";

	private final Map<String, Object> singletonBeans;

	@Construct 
	InjectionContext() {
		this(new HashMap<>());
	}

	InjectionContext(InjectionContext baseContext) {
		this.singletonBeans = new HashMap<>();
		if (baseContext != null) {
			this.singletonBeans.putAll(baseContext.singletonBeans);
		}
		this.singletonBeans.put(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, this);
	}
	
	private InjectionContext(Map<String, Object> singletonBeans) {
		this.singletonBeans = singletonBeans;
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
	
	@SuppressWarnings("unchecked")
	<T> T removeSingleton(String singletonId) {
		return (T) this.singletonBeans.remove(singletonId);
	}
}