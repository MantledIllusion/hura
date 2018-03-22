package com.mantledillusion.injection.hura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.exception.ProcessorException;

class InjectionContext {
	
	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";
	
	static class GlobalInjectionContext extends InjectionContext {

		private final Map<String, List<SelfSustaningProcessor>> destroyables = new HashMap<>();
		
		GlobalInjectionContext() {
		}
		
		<T> void addGlobalSingleton(String singletonId, T instance, List<Processor<? super T>> destroyers) {
			List<SelfSustaningProcessor> destroyables = new ArrayList<>();
			for (Processor<? super T> destroyer : destroyers) {
				destroyables.add(() -> destroyer.process(instance, null));
			}
			this.destroyables.put(singletonId, destroyables);
			super.addSingleton(singletonId, instance);
		}
		
		void destroy() {
			Iterator<Entry<String, List<SelfSustaningProcessor>>> iter = this.destroyables.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, List<SelfSustaningProcessor>> entry = iter.next();
				try {
					for (SelfSustaningProcessor destroyable: entry.getValue()) {
						destroyable.process();
					}
					super.removeSingleton(entry.getKey());
				} catch (Exception e) {
					throw new ProcessorException("Unable to destroy global singleton '"+entry.getKey()+"'", e);
				}
			}
		}
	}

	private final Map<String, Object> singletonBeans;
	
	private InjectionContext() {
		this(null, null);
	}
	
	InjectionContext(ResolvingContext resolvingContext) {
		this(null, resolvingContext);
	}

	InjectionContext(InjectionContext baseContext, ResolvingContext resolvingContext) {
		this.singletonBeans = new HashMap<>();
		if (baseContext != null) {
			this.singletonBeans.putAll(baseContext.singletonBeans);
		}
		this.singletonBeans.put(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, this);
		if (resolvingContext != null) {
			this.singletonBeans.put(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID, resolvingContext);
		}
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