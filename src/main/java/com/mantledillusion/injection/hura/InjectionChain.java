package com.mantledillusion.injection.hura;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.InjectionContext.GlobalInjectionContext;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

final class InjectionChain {

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

	// Root Blueprint
	private final Map<Type, AbstractAllocator<?>> typeAllocations;
	private final Map<String, AbstractAllocator<?>> sequenceSingletonAllocations;
	private final Map<String, AbstractAllocator<?>> globalSingletonAllocations;

	// Context
	private final InjectionContext context;
	private final ResolvingContext resolvingContext;
	private final MappingContext mappingContext;

	// Injection Chain
	private final LinkedHashSet<Constructor<?>> constructorChain;
	private final DependencyContext dependency;
	private final Constructor<?> dependencyConstructor;

	// Processability
	private final List<SelfSustaningProcessor> finalizables;
	private final List<SelfSustaningProcessor> destroyables;

	private InjectionChain(InjectionContext context, ResolvingContext resolvingContext, MappingContext mappingContext,
			Map<Type, AbstractAllocator<?>> typeAllocations,
			Map<String, AbstractAllocator<?>> sequenceSingletonAllocations,
			Map<String, AbstractAllocator<?>> globalSingletonAllocations,
			LinkedHashSet<Constructor<?>> constructorChain, DependencyContext dependency,
			Constructor<?> dependencyConstructor, List<SelfSustaningProcessor> finalizables,
			List<SelfSustaningProcessor> destroyables) {
		this.context = context;
		this.resolvingContext = resolvingContext;
		this.mappingContext = mappingContext;

		this.typeAllocations = typeAllocations;
		this.sequenceSingletonAllocations = sequenceSingletonAllocations;
		this.globalSingletonAllocations = globalSingletonAllocations;

		this.constructorChain = constructorChain;
		this.dependency = dependency;
		this.dependencyConstructor = dependencyConstructor;

		this.finalizables = finalizables;
		this.destroyables = destroyables;
	}

	InjectionChain extendBy(Blueprint blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>(this.typeAllocations);
		Map<String, AbstractAllocator<?>> sequenceSingletonAllocations = new HashMap<>(
				this.sequenceSingletonAllocations);

		typeAllocations.putAll(blueprint.getTypeAllocations());
		sequenceSingletonAllocations.putAll(blueprint.getSingletonAllocations());
		ResolvingContext resolvingContext = this.resolvingContext.merge(blueprint.getPropertyAllocations());
		MappingContext mappingContext = this.mappingContext.merge(blueprint.getMappingAllocations());

		return new InjectionChain(this.context, resolvingContext, mappingContext, typeAllocations,
				sequenceSingletonAllocations, this.globalSingletonAllocations, this.constructorChain, this.dependency,
				this.dependencyConstructor, this.finalizables, this.destroyables);
	}

	InjectionChain adjustBy(List<Blueprint> adjustments) {
		Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
		ResolvingContext resolvingContext = new ResolvingContext();
		MappingContext mappingContext = new MappingContext();

		for (Blueprint extension : adjustments) {
			typeAllocations.putAll(extension.getTypeAllocations());
			resolvingContext = resolvingContext.merge(extension.getPropertyAllocations());
			mappingContext = mappingContext.merge(extension.getMappingAllocations());
		}

		typeAllocations.putAll(this.typeAllocations);
		resolvingContext = resolvingContext.merge(this.resolvingContext);
		mappingContext = mappingContext.merge(this.mappingContext);

		return new InjectionChain(this.context, resolvingContext, mappingContext, typeAllocations,
				this.sequenceSingletonAllocations, this.globalSingletonAllocations, this.constructorChain, this.dependency,
				this.dependencyConstructor, this.finalizables, this.destroyables);
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

		return new InjectionChain(this.context, this.resolvingContext, this.mappingContext, this.typeAllocations,
				this.sequenceSingletonAllocations, this.globalSingletonAllocations, constructorChain, dependency,
				dependencyConstructor, this.finalizables, this.destroyables);
	}

	static InjectionChain forInjection(InjectionContext injectionContext, ResolvingContext resolvingContext,
			MappingContext mappingContext, Map<Type, AbstractAllocator<?>> typeAllocations,
			Map<String, AbstractAllocator<?>> sequenceSingletonAllocations) {
		return new InjectionChain(injectionContext, resolvingContext, mappingContext, typeAllocations,
				sequenceSingletonAllocations, new HashMap<>(), new LinkedHashSet<>(), DependencyContext.INDEPENDENT,
				null, new ArrayList<>(), new ArrayList<>());
	}

	static InjectionChain forGlobalSingletonResolving(GlobalInjectionContext globalInjectionContext) {
		return forGlobalSingletonResolving(globalInjectionContext, new HashMap<>());
	}

	static InjectionChain forGlobalSingletonResolving(GlobalInjectionContext globalInjectionContext,
			Map<String, AbstractAllocator<?>> globalSingletonAllocations) {
		ResolvingContext resolvingContext = globalInjectionContext.retrieveSingleton(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID);
		MappingContext mappingContext = globalInjectionContext.retrieveSingleton(MappingContext.MAPPING_CONTEXT_SINGLETON_ID);
		
		return new InjectionChain(new InjectionContext(resolvingContext, mappingContext), resolvingContext,
				mappingContext, new HashMap<>(), new HashMap<>(), globalSingletonAllocations, new LinkedHashSet<>(),
				DependencyContext.INDEPENDENT, null, new ArrayList<>(), new ArrayList<>());
	}

	// Blueprint
	boolean hasTypeAllocator(Type type) {
		return this.typeAllocations.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getTypeAllocator(Type type) {
		return (AbstractAllocator<T>) this.typeAllocations.get(type);
	}

	boolean hasSequenceSingletonAllocator(String singletonId) {
		return this.sequenceSingletonAllocations.containsKey(singletonId);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getSequenceSingletonAllocator(String singletonId) {
		return (AbstractAllocator<T>) this.sequenceSingletonAllocations.get(singletonId);
	}

	boolean hasGlobalSingletonAllocator(String singletonId) {
		return this.globalSingletonAllocations.containsKey(singletonId);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getGlobalSingletonAllocator(String singletonId) {
		return (AbstractAllocator<T>) this.globalSingletonAllocations.get(singletonId);
	}

	// Injection Context
	boolean hasSingleton(String singletonId, Class<?> type, boolean allocatedOnly) {
		return this.context.hasSingleton(singletonId, type, allocatedOnly);
	}

	<T> void addSingleton(String singletonId, T instance, boolean isAllocated) {
		this.context.addSingleton(singletonId, instance, isAllocated);
	}

	<T> T retrieveSingleton(String singletonId) {
		return this.context.retrieveSingleton(singletonId);
	}

	// Resolving Context
	boolean hasProperty(String propertyKey) {
		return this.resolvingContext.hasProperty(propertyKey);
	}

	String getProperty(String propertyKey) {
		return this.resolvingContext.getProperty(propertyKey);
	}
	
	// Mapping Context
	boolean hasMapping(String singletonId, SingletonMode mode) {
		return this.mappingContext.hasMapping(singletonId, mode);
	}
	
	String map(String singletonId, SingletonMode mode) {
		return this.mappingContext.map(singletonId, mode);
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