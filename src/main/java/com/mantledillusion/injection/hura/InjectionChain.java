package com.mantledillusion.injection.hura;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.InjectionContext.GlobalInjectionContext;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.Global.SingletonMode;
import com.mantledillusion.injection.hura.exception.InjectionException;

final class InjectionChain {

	private static final class InjectionLock {

		private final IdentityHashMap<Object, ChainLock> locks;

		private InjectionLock() {
			this.locks = new IdentityHashMap<Object, ChainLock>();
		}

		private void register(InjectionChain chain) {
			if (this.locks.containsKey(chain.context.getInjectionTreeLock())) {
				ChainLock lock = this.locks.get(chain.context.getInjectionTreeLock());
				if (lock.injectionSequenceLock != chain.chainLock.injectionSequenceLock) {
					throw new InjectionException(
							"Cannot begin a new injection sequence during another sequence already running. Use "
									+ TemporalInjectorCallback.class.getSimpleName()
									+ " for manually triggered in-sequence injection.");
				}
			} else {
				this.locks.put(chain.context.getInjectionTreeLock(), chain.chainLock);
			}
		}

		private boolean isFromChain(InjectionChain chain) {
			return this.locks.containsKey(chain.context.getInjectionTreeLock())
					&& this.locks.get(chain.context.getInjectionTreeLock()).isFromChain(chain);
		}

		private void unregister(InjectionChain chain) {
			this.locks.remove(chain.context.getInjectionTreeLock());
		}
	}

	private final class ChainLock {

		private final Object injectionSequenceLock;

		private ChainLock() {
			this.injectionSequenceLock = new Object();
		}

		private ChainLock(ChainLock parentLock) {
			this.injectionSequenceLock = parentLock.injectionSequenceLock;
		}

		private boolean isFromChain(InjectionChain other) {
			return other == InjectionChain.this;
		}
	}

	private static final ThreadLocal<InjectionLock> THREAD_INJECTION_LOCK = new ThreadLocal<>();

	private static enum DependencyContext {
		INDEPENDENT, SEQUENCE, GLOBAL;

		private static DependencyContext of(boolean isIndependent, SingletonMode mode) {
			if (isIndependent) {
				return INDEPENDENT;
			} else if (mode == SingletonMode.SEQUENCE) {
				return DependencyContext.SEQUENCE;
			} else {
				return GLOBAL;
			}
		}
	}

	// Singleton
	private final Map<String, AbstractAllocator<?>> sequenceSingletonAllocations;
	private final Map<String, AbstractAllocator<?>> globalSingletonAllocations;

	// Context
	private final InjectionContext context;
	private final ResolvingContext resolvingContext;
	private final MappingContext mappingContext;
	private final TypeContext typeContext;

	// Injection Chain
	private final ChainLock chainLock;
	private final LinkedHashSet<Constructor<?>> constructorChain;
	private final DependencyContext dependency;
	private final Constructor<?> dependencyConstructor;

	// Processability
	private final List<SelfSustaningProcessor> finalizables;
	private final List<SelfSustaningProcessor> destroyables;

	private InjectionChain(InjectionContext context, ResolvingContext resolvingContext, MappingContext mappingContext,
			TypeContext typeContext,
			Map<String, AbstractAllocator<?>> sequenceSingletonAllocations,
			Map<String, AbstractAllocator<?>> globalSingletonAllocations, ChainLock chainLock,
			LinkedHashSet<Constructor<?>> constructorChain, DependencyContext dependency,
			Constructor<?> dependencyConstructor, List<SelfSustaningProcessor> finalizables,
			List<SelfSustaningProcessor> destroyables) {
		this.context = context;
		this.resolvingContext = resolvingContext;
		this.mappingContext = mappingContext;
		this.typeContext = typeContext;
		
		this.sequenceSingletonAllocations = sequenceSingletonAllocations;
		this.globalSingletonAllocations = globalSingletonAllocations;

		if (chainLock == null) {
			this.chainLock = new ChainLock();
		} else {
			this.chainLock = new ChainLock(chainLock);
		}
		this.constructorChain = constructorChain;
		this.dependency = dependency;
		this.dependencyConstructor = dependencyConstructor;

		this.finalizables = finalizables;
		this.destroyables = destroyables;
	}

	void hookOnThread() {
		if (THREAD_INJECTION_LOCK.get() == null) {
			THREAD_INJECTION_LOCK.set(new InjectionLock());
		}
		THREAD_INJECTION_LOCK.get().register(this);
	}

	void unhookFromThread() {
		if (THREAD_INJECTION_LOCK.get().isFromChain(this)) {
			clearHook();
		}
	}

	void clearHook() {
		if (THREAD_INJECTION_LOCK.get() != null) {
			THREAD_INJECTION_LOCK.get().unregister(this);
		}
	}

	InjectionChain extendBy(Blueprint blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		
		Map<String, AbstractAllocator<?>> sequenceSingletonAllocations = new HashMap<>(
				this.sequenceSingletonAllocations);
		sequenceSingletonAllocations.putAll(blueprint.getSingletonAllocations());
		
		ResolvingContext resolvingContext = this.resolvingContext.merge(blueprint.getPropertyAllocations());
		MappingContext mappingContext = this.mappingContext.merge(blueprint.getMappingAllocations());
		TypeContext typeContext = this.typeContext.merge(blueprint.getTypeAllocations());

		return new InjectionChain(this.context, resolvingContext, mappingContext, typeContext,
				sequenceSingletonAllocations, this.globalSingletonAllocations, this.chainLock, this.constructorChain,
				this.dependency, this.dependencyConstructor, this.finalizables, this.destroyables);
	}

	InjectionChain adjustBy(List<Blueprint> adjustments) {
		ResolvingContext resolvingContext = new ResolvingContext();
		MappingContext mappingContext = new MappingContext();
		TypeContext typeContext = new TypeContext();

		for (Blueprint extension : adjustments) {
			resolvingContext = resolvingContext.merge(extension.getPropertyAllocations());
			mappingContext = mappingContext.merge(extension.getMappingAllocations());
			typeContext = typeContext.merge(extension.getTypeAllocations());
		}
		
		resolvingContext = this.resolvingContext.merge(resolvingContext);
		mappingContext = this.mappingContext.merge(mappingContext);
		typeContext = this.typeContext.merge(typeContext);

		return new InjectionChain(this.context, resolvingContext, mappingContext, typeContext,
				this.sequenceSingletonAllocations, this.globalSingletonAllocations, this.chainLock,
				this.constructorChain, this.dependency, this.dependencyConstructor, this.finalizables,
				this.destroyables);
	}

	InjectionChain extendBy(Constructor<?> c, InjectionSettings<?> set) {
		DependencyContext dependency = DependencyContext.of(set.isIndependent, set.singletonMode);
		Constructor<?> dependencyConstructor = this.dependencyConstructor;
		if (this.dependency.ordinal() > dependency.ordinal()) {
			dependency = this.dependency;
		} else {
			dependencyConstructor = c;
		}

		LinkedHashSet<Constructor<?>> constructorChain = new LinkedHashSet<>(this.constructorChain);
		constructorChain.add(c);

		return new InjectionChain(this.context, this.resolvingContext, this.mappingContext, this.typeContext,
				this.sequenceSingletonAllocations, this.globalSingletonAllocations, this.chainLock, constructorChain,
				dependency, dependencyConstructor, this.finalizables, this.destroyables);
	}

	static InjectionChain forInjection(InjectionContext injectionContext, ResolvingContext resolvingContext,
			MappingContext mappingContext, TypeContext typeContext,
			Map<String, AbstractAllocator<?>> sequenceSingletonAllocations) {
		return new InjectionChain(injectionContext, resolvingContext, mappingContext, typeContext,
				sequenceSingletonAllocations, new HashMap<>(), null, new LinkedHashSet<>(),
				DependencyContext.INDEPENDENT, null, new ArrayList<>(), new ArrayList<>());
	}

	static InjectionChain forGlobalSingletonResolving(GlobalInjectionContext globalInjectionContext,
			InjectionChain parent) {
		return forGlobalSingletonResolving(globalInjectionContext, new HashMap<>(), parent.chainLock);
	}

	static InjectionChain forGlobalSingletonResolving(GlobalInjectionContext globalInjectionContext,
			Map<String, AbstractAllocator<?>> globalSingletonAllocations) {
		return forGlobalSingletonResolving(globalInjectionContext, globalSingletonAllocations, null);
	}

	private static InjectionChain forGlobalSingletonResolving(GlobalInjectionContext globalInjectionContext,
			Map<String, AbstractAllocator<?>> globalSingletonAllocations, ChainLock chainLock) {
		ResolvingContext resolvingContext = globalInjectionContext
				.retrieveSingleton(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID);
		MappingContext mappingContext = globalInjectionContext
				.retrieveSingleton(MappingContext.MAPPING_CONTEXT_SINGLETON_ID);
		TypeContext typeContext = globalInjectionContext
				.retrieveSingleton(TypeContext.TYPE_CONTEXT_SINGLETON_ID);

		return new InjectionChain(
				new InjectionContext(globalInjectionContext.getInjectionTreeLock(), resolvingContext, mappingContext, typeContext),
				resolvingContext, mappingContext, typeContext, new HashMap<>(), globalSingletonAllocations,
				chainLock, new LinkedHashSet<>(), DependencyContext.INDEPENDENT, null, new ArrayList<>(),
				new ArrayList<>());
	}

	// Singleton
	boolean hasSequenceSingletonAllocator(String qualifier) {
		return this.sequenceSingletonAllocations.containsKey(qualifier);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getSequenceSingletonAllocator(String qualifier) {
		return (AbstractAllocator<T>) this.sequenceSingletonAllocations.get(qualifier);
	}

	boolean hasGlobalSingletonAllocator(String qualifier) {
		return this.globalSingletonAllocations.containsKey(qualifier);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getGlobalSingletonAllocator(String qualifier) {
		return (AbstractAllocator<T>) this.globalSingletonAllocations.get(qualifier);
	}

	// Injection Context
	boolean hasSingleton(String qualifier, Class<?> type, boolean allocatedOnly) {
		return this.context.hasSingleton(qualifier, type, allocatedOnly);
	}

	<T> void addSingleton(String qualifier, T instance, boolean isAllocated) {
		this.context.addSingleton(qualifier, instance, isAllocated);
	}

	<T> T retrieveSingleton(String qualifier) {
		return this.context.retrieveSingleton(qualifier);
	}

	// Resolving Context
	String resolve(ResolvingSettings set) {
		return this.resolvingContext.resolve(set);
	}

	// Mapping Context
	boolean hasMapping(String qualifier, SingletonMode mode) {
		return this.mappingContext.hasMapping(qualifier, mode);
	}

	String map(String qualifier, SingletonMode mode) {
		return this.mappingContext.map(qualifier, mode);
	}
	
	// Type Context
	boolean hasTypeAllocator(Type type) {
		return this.typeContext.hasTypeAllocator(type);
	}

	<T> AbstractAllocator<T> getTypeAllocator(Type type) {
		return this.typeContext.getTypeAllocator(type);
	}

	// Injection Chain
	boolean containsConstructor(Constructor<?> c) {
		return this.constructorChain.contains(c);
	}

	boolean isChildOfGlobalSingleton() {
		return this.dependency == DependencyContext.GLOBAL;
	}

	String getStringifiedChainSinceConstructor(Constructor<?> c) {
		int index = IteratorUtils.indexOf(this.constructorChain.iterator(), constructor -> constructor == c);
		return StringUtils.join(IteratorUtils.skippingIterator(this.constructorChain.iterator(), index), " -> ");
	}

	String getStringifiedChainSinceDependency() {
		return getStringifiedChainSinceConstructor(this.dependencyConstructor);
	}

	// Processables
	void addFinalizable(SelfSustaningProcessor finalizable) {
		this.finalizables.add(finalizable);
	}

	List<SelfSustaningProcessor> getFinalizables() {
		return this.finalizables;
	}

	void addDestoryable(SelfSustaningProcessor destroyable) {
		this.destroyables.add(destroyable);
	}

	List<SelfSustaningProcessor> getDestroyables() {
		return this.destroyables;
	}
}