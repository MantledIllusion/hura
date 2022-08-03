package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.core.Injector.SelfSustainingProcessor;
import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.exception.BlueprintException;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
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
	private final AliasContext aliasContext;
	private final TypeContext typeContext;

	// Injection Chain
	private final ChainLock chainLock;
	private final LinkedHashSet<Executable> executableChain;
	private final DependencyContext dependency;
	private final Bus.EventBackbone eventBackbone;

	// Processability
	private final List<SelfSustainingProcessor> aggregateables;
	private final List<SelfSustainingProcessor> activateables;
	private final List<SelfSustainingProcessor> postConstructables;
	private final List<SelfSustainingProcessor> preDestroyables;
	private final List<SelfSustainingProcessor> postDestroyables;

	private InjectionChain(SingletonContext singletonContext, ResolvingContext resolvingContext, AliasContext aliasContext,
						   TypeContext typeContext,
						   Map<String, AbstractAllocator<?>> sequenceSingletonAllocations, ChainLock chainLock,
						   LinkedHashSet<Executable> executableChain, DependencyContext dependency,
						   Bus.EventBackbone eventBackbone, List<SelfSustainingProcessor> aggregateables,
						   List<SelfSustainingProcessor> activatables, List<SelfSustainingProcessor> postConstructables,
						   List<SelfSustainingProcessor> preDestroyables, List<SelfSustainingProcessor> postDestroyables) {
		this.singletonContext = singletonContext;
		this.resolvingContext = resolvingContext;
		this.aliasContext = aliasContext;
		this.typeContext = typeContext;
		
		this.sequenceSingletonAllocations = sequenceSingletonAllocations;

		if (chainLock == null) {
			this.chainLock = new ChainLock();
		} else {
			this.chainLock = new ChainLock(chainLock);
		}
		this.executableChain = executableChain;
		this.dependency = dependency;
		this.eventBackbone = eventBackbone;

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
		AliasContext aliasContext = this.aliasContext.merge(allocations.getAliasAllocations());
		TypeContext typeContext = this.typeContext.merge(allocations.getTypeAllocations());

		return new InjectionChain(this.singletonContext, resolvingContext, aliasContext, typeContext,
				new HashMap<>(this.sequenceSingletonAllocations), this.chainLock,
				this.executableChain, this.dependency,
				this.eventBackbone, this.aggregateables,
				this.activateables, this.postConstructables,
				this.preDestroyables, this.postDestroyables);
	}

	InjectionChain extendBy(Constructor<?> c, InjectionSettings<?> set) {
		if (this.executableChain.contains(c)) {
			throw new InjectionException("Injection dependency cycle detected: " + getStringifiedChainSinceExecutable(c));
		}

		DependencyContext dependency = DependencyContext.of(set.isIndependent);
		if (this.dependency.ordinal() > dependency.ordinal()) {
			dependency = this.dependency;
		}

		LinkedHashSet<Executable> executableChain = new LinkedHashSet<>(this.executableChain);
		executableChain.add(c);

		return new InjectionChain(this.singletonContext, this.resolvingContext, this.aliasContext, this.typeContext,
				this.sequenceSingletonAllocations, this.chainLock,
				executableChain, dependency,
				this.eventBackbone, this.aggregateables,
				this.activateables, this.postConstructables,
				this.preDestroyables, this.postDestroyables);
	}

	InjectionChain extendBy(Method m, InjectionSettings<?> set) {
		if (this.executableChain.contains(m)) {
			throw new InjectionException("Injection dependency cycle detected: " + getStringifiedChainSinceExecutable(m));
		}

		DependencyContext dependency = DependencyContext.of(set.isIndependent);
		if (this.dependency.ordinal() > dependency.ordinal()) {
			dependency = this.dependency;
		}

		LinkedHashSet<Executable> executableChain = new LinkedHashSet<>(this.executableChain);
		executableChain.add(m);

		return new InjectionChain(this.singletonContext, this.resolvingContext, this.aliasContext, this.typeContext,
				this.sequenceSingletonAllocations, this.chainLock,
				executableChain, dependency,
				this.eventBackbone, this.aggregateables,
				this.activateables, this.postConstructables,
				this.preDestroyables, this.postDestroyables);
	}

	private String getStringifiedChainSinceExecutable(Executable e) {
		StringBuilder sb = new StringBuilder();
		this.executableChain.forEach(constructor -> {
			if (constructor == e || sb.length() > 0) {
				if (sb.length() > 0) {
					sb.append(" -> ");
				}
				sb.append(e).append(e.toString());
			}
		});
		return sb.toString();
	}

	static InjectionChain forRoot(InjectionAllocations allocations) {
		ResolvingContext resolvingContext = new ResolvingContext().merge(allocations.getPropertyAllocations());
		AliasContext aliasContext = new AliasContext().merge(allocations.getAliasAllocations());
		TypeContext typeContext = new TypeContext().merge(allocations.getTypeAllocations());
		SingletonContext singletonContext = new SingletonContext(new Object(),
				resolvingContext, aliasContext, typeContext);

		Bus.EventBackbone backbone = new Bus.EventBackbone();
		singletonContext.addSingleton(Bus.QUALIFIER_BACKBONE, backbone, true, true);

		return new InjectionChain(singletonContext, resolvingContext, aliasContext, typeContext,
				allocations.getSingletonAllocations(), null,
				new LinkedHashSet<>(), DependencyContext.INDEPENDENT,
				backbone, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>());
	}

	static InjectionChain forInjection(Object injectionTreeLock, SingletonContext baseSingletonContext,
									   ResolvingContext baseResolvingContext, AliasContext baseAliasContext,
									   TypeContext baseTypeContext, InjectionAllocations allocations) {
		ResolvingContext resolvingContext = baseResolvingContext.merge(allocations.getPropertyAllocations());
		AliasContext aliasContext = baseAliasContext.merge(allocations.getAliasAllocations());
		TypeContext typeContext = baseTypeContext.merge(allocations.getTypeAllocations());
		SingletonContext singletonContext = new SingletonContext(injectionTreeLock, baseSingletonContext,
				resolvingContext, aliasContext, typeContext);

		Bus.EventBackbone backbone = new Bus.EventBackbone(singletonContext.retrieveSingleton(Bus.QUALIFIER_BACKBONE),
				resolvingContext.getProperty(Bus.PROPERTY_BUS_ISOLATION));
		singletonContext.addSingleton(Bus.QUALIFIER_BACKBONE, backbone, true, true);
		List<SelfSustainingProcessor> postDestroyables = new ArrayList<>();
		postDestroyables.add(backbone::detachFromParent);

		return new InjectionChain(singletonContext, resolvingContext, aliasContext, typeContext,
				allocations.getSingletonAllocations(), null,
				new LinkedHashSet<>(), DependencyContext.INDEPENDENT,
				backbone, new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), postDestroyables);
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

	void removeSingletonAllocator(String qualifier) {
		this.sequenceSingletonAllocations.remove(qualifier);
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

	<T> Collection<T> aggregate(Class<T> type, Collection<BiPredicate<String, T>> biPredicates) {
		return this.singletonContext.aggregate(type, biPredicates);
	}

	// Resolving Context
	ResolvingContext getResolvingContext() {
		return this.resolvingContext;
	}

	<T> T resolve(ResolvingSettings<T> set) {
		return this.resolvingContext.resolve(set);
	}

	// Alias Context
	AliasContext getAliasContext() {
		return this.aliasContext;
	}

	boolean hasMapping(String qualifier) {
		return this.aliasContext.hasMapping(qualifier);
	}

	String map(String qualifier) {
		return this.aliasContext.getAlias(qualifier);
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

	Bus.EventBackbone getEventBackbone() {
		return this.eventBackbone;
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