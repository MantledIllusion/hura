package com.mantledillusion.injection.hura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.exception.ProcessorException;

class GlobalInjectionContext extends InjectionContext {

	private final Map<String, List<SelfSustaningProcessor>> destroyables = new HashMap<>();
	
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
				throw new ProcessorException("Unable to destroy global singleton '"+entry.getKey()+"': "+e.getMessage(), e);
			}
		}
	}
}
