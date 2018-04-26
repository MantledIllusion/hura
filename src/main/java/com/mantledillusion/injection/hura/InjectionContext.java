package com.mantledillusion.injection.hura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.exception.ProcessorException;

class InjectionContext {

	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";

	static class GlobalInjectionContext extends InjectionContext {

		private final Map<String, List<SelfSustaningProcessor>> destroyables = new HashMap<>();

		GlobalInjectionContext(ResolvingContext resolvingContext, MappingContext mappingContext) {
			super(resolvingContext, mappingContext);
		}

		<T> void addGlobalSingleton(String singletonId, T instance, List<Processor<? super T>> destroyers) {
			List<SelfSustaningProcessor> destroyables = new ArrayList<>();
			for (Processor<? super T> destroyer : destroyers) {
				destroyables.add(() -> destroyer.process(instance, null));
			}
			this.destroyables.put(singletonId, destroyables);
			super.addSingleton(singletonId, instance, true);
		}

		void destroy() {
			Iterator<Entry<String, List<SelfSustaningProcessor>>> iter = this.destroyables.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, List<SelfSustaningProcessor>> entry = iter.next();
				try {
					for (SelfSustaningProcessor destroyable : entry.getValue()) {
						destroyable.process();
					}
					super.removeSingleton(entry.getKey());
				} catch (Exception e) {
					throw new ProcessorException("Unable to destroy global singleton '" + entry.getKey() + "'", e);
				}
			}
		}
	}

	private final Map<String, Pair<Object, Boolean>> singletonBeans;

	InjectionContext(ResolvingContext resolvingContext, MappingContext mappingContext) {
		this(null, resolvingContext, mappingContext);
	}

	InjectionContext(InjectionContext baseContext, ResolvingContext resolvingContext, MappingContext mappingContext) {
		this.singletonBeans = new HashMap<>();
		if (baseContext != null) {
			/*
			 * Singleton from a base context are singletons from a parent injection context,
			 * so they are treated as allocated; they cannot be changed anymore, so order of
			 * injection within an injection sequence is not relevant any more.
			 */
			for (String singletonId : baseContext.singletonBeans.keySet()) {
				this.singletonBeans.put(singletonId,
						Pair.of(baseContext.singletonBeans.get(singletonId).getLeft(), Boolean.TRUE));
			}
		}
		addSingleton(INJECTION_CONTEXT_SINGLETON_ID, this, false);
		if (resolvingContext != null) {
			addSingleton(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID, resolvingContext, false);
		}
		if (mappingContext != null) {
			addSingleton(MappingContext.MAPPING_CONTEXT_SINGLETON_ID, mappingContext, false);
		}
	}

	boolean hasSingleton(String singletonId, Class<?> type, boolean allocatedOnly) {
		if (this.singletonBeans.containsKey(singletonId)) {
			if (this.singletonBeans.get(singletonId).getRight()) {
				return true;
			} else if (allocatedOnly) {
				return false;
			} else if (this.singletonBeans.get(singletonId).getLeft() == null
					|| this.singletonBeans.get(singletonId).getLeft().getClass() == type) {
				return true;
			} else {
				/*
				 * If the singleton was not allocated, it was created on demand by injecting the
				 * type of the target. This is okay as long as every target of the singletonId
				 * has the same type or the origin of the singleton already created is from the
				 * parent injection sequence.
				 * 
				 * But if 2 or more on demand singleton injections of the same singletonId are
				 * done in the same injection sequence, it is crucial that the type used for on
				 * demand injection is exactly the same. If not, the order of the injection in
				 * that very sequence could determine a different outcome of the injection
				 * sequence, causing unpredictable behavior.
				 */
				throw new InjectionException("A singleton of the type '"
						+ this.singletonBeans.get(singletonId).getLeft().getClass().getSimpleName()
						+ "' was created on demand for the singletonId '" + singletonId
						+ "' instead of being allocated. As a result, it can be only injected into "
						+ "targets of the same type in its injection sequence, but the same singletonId "
						+ "is also required for a target of the type '" + type.getSimpleName() + "'.");
			}
		} else {
			return false;
		}
	}

	<T> void addSingleton(String singletonId, T instance, boolean isAllocated) {
		this.singletonBeans.put(singletonId, Pair.of(instance, isAllocated));
	}

	@SuppressWarnings("unchecked")
	<T> T retrieveSingleton(String singletonId) {
		return (T) this.singletonBeans.get(singletonId).getLeft();
	}

	@SuppressWarnings("unchecked")
	<T> T removeSingleton(String singletonId) {
		return (T) this.singletonBeans.remove(singletonId);
	}
}