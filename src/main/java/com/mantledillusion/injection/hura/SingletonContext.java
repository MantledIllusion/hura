package com.mantledillusion.injection.hura;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.mantledillusion.injection.hura.exception.InjectionException;

class SingletonContext {
	
	static final String INJECTION_CONTEXT_SINGLETON_ID = "_injectionContext";

	private final Object injectionTreeLock;
	private final Map<String, Pair<Object, Boolean>> singletonBeans;

	SingletonContext(Object injectionTreeLock, ResolvingContext resolvingContext, MappingContext mappingContext, TypeContext typeContext) {
		this(injectionTreeLock, null, resolvingContext, mappingContext, typeContext);
	}

	SingletonContext(Object injectionTreeLock, SingletonContext baseContext, ResolvingContext resolvingContext, MappingContext mappingContext, TypeContext typeContext) {
		this.injectionTreeLock = injectionTreeLock;
		this.singletonBeans = new HashMap<>();
		if (baseContext != null) {
			/*
			 * SingletonAllocation from a base context are singletons from a parent injection context,
			 * so they are treated as allocated; they cannot be changed anymore, so order of
			 * injection within an injection sequence is not relevant any more.
			 */
			for (String qualifier : baseContext.singletonBeans.keySet()) {
				this.singletonBeans.put(qualifier,
						Pair.of(baseContext.singletonBeans.get(qualifier).getLeft(), Boolean.TRUE));
			}
		}
		addSingleton(INJECTION_CONTEXT_SINGLETON_ID, this, false);
		if (resolvingContext != null) {
			addSingleton(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID, resolvingContext, false);
		}
		if (mappingContext != null) {
			addSingleton(MappingContext.MAPPING_CONTEXT_SINGLETON_ID, mappingContext, false);
		}
		if (typeContext != null) {
			addSingleton(TypeContext.TYPE_CONTEXT_SINGLETON_ID, typeContext, false);
		}
	}

	Object getInjectionTreeLock() {
		return injectionTreeLock;
	}

	boolean hasSingleton(String qualifier, Class<?> type, boolean allocatedOnly) {
		if (this.singletonBeans.containsKey(qualifier)) {
			if (this.singletonBeans.get(qualifier).getRight()) {
				return true;
			} else if (allocatedOnly) {
				return false;
			} else if (this.singletonBeans.get(qualifier).getLeft() == null
					|| this.singletonBeans.get(qualifier).getLeft().getClass() == type) {
				return true;
			} else {
				/*
				 * If the singleton was not allocated, it was created on demand by injecting the
				 * type of the target. This is okay as long as every target of the qualifier
				 * has the same type or the origin of the singleton already created is from the
				 * parent injection sequence.
				 * 
				 * But if 2 or more on demand singleton injections of the same qualifier are
				 * done in the same injection sequence, it is crucial that the type used for on
				 * demand injection is exactly the same. If not, the order of the injection in
				 * that very sequence could determine a different outcome of the injection
				 * sequence, causing unpredictable behavior.
				 */
				throw new InjectionException("A singleton of the type '"
						+ this.singletonBeans.get(qualifier).getLeft().getClass().getSimpleName()
						+ "' was created on demand for the qualifier '" + qualifier
						+ "' instead of being allocated. As a result, it can be only injected into "
						+ "targets of the same type in its injection sequence, but the same qualifier "
						+ "is also required for a target of the type '" + type.getSimpleName() + "'.");
			}
		} else {
			return false;
		}
	}

	<T> void addSingleton(String qualifier, T instance, boolean isAllocated) {
		this.singletonBeans.put(qualifier, Pair.of(instance, isAllocated));
	}

	@SuppressWarnings("unchecked")
	<T> T retrieveSingleton(String qualifier) {
		return (T) this.singletonBeans.get(qualifier).getLeft();
	}

	@SuppressWarnings("unchecked")
	<T> T removeSingleton(String qualifier) {
		return (T) this.singletonBeans.remove(qualifier);
	}
}