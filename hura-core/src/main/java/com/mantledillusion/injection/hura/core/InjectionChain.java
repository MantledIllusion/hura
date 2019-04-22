package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.core.Injector.SelfSustainingProcessor;
import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.exception.BlueprintException;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiPredicate;

final class InjectionChain {

	private static final class InjectionLock {

		private final IdentityHashMap<Object, ChainLock> locks;

		private InjectionLock() {
			this.locks = new IdentityHashMap<>();
		}

		private void register(InjectionChain chain) {
			if (this.locks.containsKey(chain.singletonContext.getInjectionTreeLock())) {
				ChainLock lock = this.locks.get(chain.singletonContext.getInjectionTreeLock());
				if (lock.injectionSequenceLock != chain.chainLock.injectionSequenceLock) {
					throw new InjectionException(
							"Cannot begin a new injection sequence during another sequence already running. Use "
									+ TemporalInjectorCallback.class.getSimpleName()
									+ " for manually triggered in-sequence injection.");
				}
			} else {
				this.locks.put(chain.singletonContext.getInjectionTreeLock(), chain.chainLock);
			}
		}

		private boolean isFromChain(InjectionChain chain) {
			return this.locks.containsKey(chain.singletonContext.getInjectionTreeLock())
					&& this.locks.get(chain.singletonContext.getInjectionTreeLock()).isFromChain(chain);
		}

		private void unregister(InjectionChain chain) {
			this.locks.remove(chain.singletonContext.getInjectionTreeLock());
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

	private enum DependencyContext {
		INDEPENDENT, SEQUENCE;

		private static DependencyContext of(boolean isIndependent) {
			if (isIndependent) {
				return INDEPENDENT;
			} else {
				return SEQUENCE;
			}
		}
	}

	// SingletonAllocation
	private final Map<String, AbstractAllocator<?>> sequenceSingletonAllocations;

	// Context
	private final SingletonContext singletonContext;
	private final ResolvingContext resolvingContext;
	private final MappingContext mappingContext;
	private final TypeContext typeContext;

	// Injection Chain
	private final ChainLock chainLock;
	private final LinkedHashSet<Constructor<?>> constructorChain;
	private final DependencyContext dependency;
	private final Constructor<?> dependencyConstructor;

	// Processability
	private final List<SelfSustainingProcessor> aggregateables;
	private final List<SelfSustainingProcessor> activateables;
	private final List<SelfSustainingProcessor> postConstructables;
	private final List<SelfSustainingProcessor> preDestroyables;
	private final List<SelfSustainingProcessor> postDestroyables;

	private InjectionChain(SingletonContext singletonContext, ResolvingContext resolvingContext, MappingContext mappingContext,
						   TypeContext typeContext,
						   Map<String, AbstractAllocator<?>> sequenceSingletonAllocations, ChainLock chainLock,
						   LinkedHashSet<Constructor<?>> constructorChain, DependencyContext dependency,
						   Constructor<?> dependencyConstructor, List<SelfSustainingProcessor> aggregateables,
						   List<SelfSustainingProcessor> activatables, List<SelfSustainingProcessor> postConstructables,
						   List<SelfSustainingProcessor> preDestroyables, List<SelfSustainingProcessor> postDestroyables) {
		this.singletonContext = singletonContext;
		this.resolvingContext = resolvingContext;
		this.mappingContext = mappingContext;
		this.typeContext = typeContext;
		
		this.sequenceSingletonAllocations = sequenceSingletonAllocations;

		if (chainLock == null) {
			this.chainLock = new ChainLock();
		} else {
			this.chainLock = new ChainLock(chainLock);
		}
		this.constructorChain = constructorChain;
		this.dependency = dependency;
		this.dependencyConstructor = dependencyConstructor;

		this.aggregateables = aggregateables;
		this.activateables = activatables;
		this.postConstructables = postConstructables;
		this.preDestroyables = preDestroyables;
		this.postDestroyables = postDestroyables;
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

	InjectionChain extendBy(InjectionAllocations allocations) {
		if (!allocations.getSingletonAllocations().isEmpty()) {
			throw new BlueprintException("There are " + allocations.getSingletonAllocations().size()
					+ " allocations for singletons [" + StringUtils.join(allocations.getSingletonAllocations().keySet(),
					", ") + "] that have been given as an adjustment during an injection sequence; singletons "
					+ "can only be allocated at the beginning of an injection sequence, their allocations cannot be adjusted.");
		}

		ResolvingContext resolvingContext = this.resolvingContext.merge(allocations.getPropertyAllocations());
		MappingContext mappingContext = this.mappingContext.merge(allocations.getMappingAllocations());
		TypeContext typeContext = this.typeContext.merge(allocations.getTypeAllocations());

		return new InjectionChain(this.singletonContext, resolvingContext, mappingContext, typeContext,
				new HashMap<>(this.sequenceSingletonAllocations), this.chainLock, this.constructorChain,
				this.dependency, this.dependencyConstructor, this.aggregateables,
				this.activateables, this.postConstructables,
				this.preDestroyables, this.postDestroyables);
	}

	InjectionChain extendBy(Constructor<?> c, InjectionSettings<?> set) {
		DependencyContext dependency = DependencyContext.of(set.isIndependent);
		Constructor<?> dependencyConstructor = this.dependencyConstructor;
		if (this.dependency.ordinal() > dependency.ordinal()) {
			dependency = this.dependency;
		} else {
			dependencyConstructor = c;
		}

		LinkedHashSet<Constructor<?>> constructorChain = new LinkedHashSet<>(this.constructorChain);
		constructorChain.add(c);

		return new InjectionChain(this.singletonContext, this.resolvingContext, this.mappingContext, this.typeContext,
				this.sequenceSingletonAllocations, this.chainLock,
				constructorChain, dependency, dependencyConstructor, this.aggregateables,
				this.activateables, this.postConstructables,
				this.preDestroyables, this.postDestroyables);
	}

	static InjectionChain forRoot(InjectionAllocations allocations) {
		ResolvingContext resolvingContext = new ResolvingContext().merge(allocations.getPropertyAllocations());
		MappingContext mappingContext = new MappingContext().merge(allocations.getMappingAllocations());
		TypeContext typeContext = new TypeContext().merge(allocations.getTypeAllocations());
		SingletonContext singletonContext = new SingletonContext(new Object(),
				resolvingContext, mappingContext, typeContext);

		return new InjectionChain(singletonContext, resolvingContext, mappingContext, typeContext,
				allocations.getSingletonAllocations(), null,
				new LinkedHashSet<>(), DependencyContext.INDEPENDENT, null, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>());
	}

	static InjectionChain forInjection(Object injectionTreeLock, SingletonContext baseSingletonContext,
									   ResolvingContext baseResolvingContext, MappingContext baseMappingContext,
									   TypeContext baseTypeContext, InjectionAllocations allocations) {
		ResolvingContext resolvingContext = baseResolvingContext.merge(allocations.getPropertyAllocations());
		MappingContext mappingContext = baseMappingContext.merge(allocations.getMappingAllocations());
		TypeContext typeContext = baseTypeContext.merge(allocations.getTypeAllocations());
		SingletonContext singletonContext = new SingletonContext(injectionTreeLock, baseSingletonContext,
				resolvingContext, mappingContext, typeContext);

		return new InjectionChain(singletonContext, resolvingContext, mappingContext, typeContext,
				allocations.getSingletonAllocations(), null,
				new LinkedHashSet<>(), DependencyContext.INDEPENDENT, null, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>());
	}

	// SingletonAllocation Allocation
	boolean hasSingletonAllocator(String qualifier) {
		return this.sequenceSingletonAllocations.containsKey(qualifier);
	}

	Set<String> getSingletonAllocations() {
		return this.sequenceSingletonAllocations.keySet();
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getSingletonAllocator(String qualifier) {
		return (AbstractAllocator<T>) this.sequenceSingletonAllocations.get(qualifier);
	}

	// SingletonAllocation Context
	SingletonContext getSingletonContext() {
		return this.singletonContext;
	}

	boolean hasSingleton(String qualifier, Class<?> type, boolean allocatedOnly) {
		return this.singletonContext.hasSingleton(qualifier, type, allocatedOnly);
	}

	<T> void addSingleton(String qualifier, T instance, boolean isAllocated) {
		this.singletonContext.addSingleton(qualifier, instance, false, isAllocated);
	}

	<T> T retrieveSingleton(String qualifier) {
		return this.singletonContext.retrieveSingleton(qualifier);
	}

	<T> Collection<T> aggregateSingletons(Class<T> type, Collection<BiPredicate<String, T>> biPredicates) {
		return this.singletonContext.aggregate(type, biPredicates);
	}

	// Resolving Context
	ResolvingContext getResolvingContext() {
		return this.resolvingContext;
	}

	String resolve(ResolvingSettings set) {
		return this.resolvingContext.resolve(set);
	}

	// MappingAllocation Context
	MappingContext getMappingContext() {
		return this.mappingContext;
	}

	boolean hasMapping(String qualifier) {
		return this.mappingContext.hasMapping(qualifier);
	}

	String map(String qualifier) {
		return this.mappingContext.getMapping(qualifier);
	}
	
	// Type Context
	TypeContext getTypeContext() {
		return this.typeContext;
	}

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

	String getStringifiedChainSinceConstructor(Constructor<?> c) {
		StringBuilder sb = new StringBuilder();
		this.constructorChain.forEach(constructor -> {
			if (constructor == c || sb.length() > 0) {
				if (sb.length() > 0) {
					sb.append(" -> ");
				}
				sb.append(c).append(c.toString());
			}
		});
		return sb.toString();
	}

	String getStringifiedChainSinceDependency() {
		return getStringifiedChainSinceConstructor(this.dependencyConstructor);
	}

	// Processables

	void addAggregateable(SelfSustainingProcessor aggregateable) {
		this.aggregateables.add(aggregateable);
	}

	List<SelfSustainingProcessor> getAggregateables() {
		return aggregateables;
	}

	void addActivateable(SelfSustainingProcessor activateable) {
		this.activateables.add(activateable);
	}

	List<SelfSustainingProcessor> getActivateables() {
		return activateables;
	}

	void addPostConstructables(SelfSustainingProcessor postConstructable) {
		this.postConstructables.add(postConstructable);
	}

	List<SelfSustainingProcessor> getPostConstructables() {
		return this.postConstructables;
	}

	void addPreDestroyable(SelfSustainingProcessor destroyable) {
		this.preDestroyables.add(destroyable);
	}

	List<SelfSustainingProcessor> getPreDestroyables() {
		return this.preDestroyables;
	}

	void addPostDestroyable(SelfSustainingProcessor destroyable) {
		this.postDestroyables.add(destroyable);
	}

	List<SelfSustainingProcessor> getPostDestroyables() {
		return this.postDestroyables;
	}
}