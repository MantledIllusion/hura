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

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
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
	private final Map<String, AbstractAllocator<?>> singletonAllocations;

	// Context
	private final InjectionContext context;
	private final ResolvingContext resolvingContext;

	// Injection Chain
	private final LinkedHashSet<Constructor<?>> constructorChain;
	private final DependencyContext dependency;
	private final Constructor<?> dependencyConstructor;

	// Processability
	private final List<SelfSustaningProcessor> finalizables;
	private final List<SelfSustaningProcessor> destroyables;

	private InjectionChain(Map<Type, AbstractAllocator<?>> typeAllocations,
			Map<String, AbstractAllocator<?>> singletonAllocations, InjectionContext baseContext,
			ResolvingContext resolvingContext) {
		this.typeAllocations = typeAllocations;
		this.singletonAllocations = singletonAllocations;

		this.resolvingContext = new ResolvingContext(resolvingContext);
		this.context = new InjectionContext(baseContext, this.resolvingContext);

		this.constructorChain = new LinkedHashSet<>();
		this.dependency = DependencyContext.INDEPENDENT;
		this.dependencyConstructor = null;

		this.finalizables = new ArrayList<>();
		this.destroyables = new ArrayList<>();
	}

	private InjectionChain(Map<Type, AbstractAllocator<?>> typeAllocations,
			Map<String, AbstractAllocator<?>> singletonAllocations, InjectionContext context,
			ResolvingContext resolvingContext, LinkedHashSet<Constructor<?>> constructorChain,
			DependencyContext dependency, Constructor<?> dependencyConstructor,
			List<SelfSustaningProcessor> finalizables, List<SelfSustaningProcessor> destroyables) {
		this.typeAllocations = typeAllocations;
		this.singletonAllocations = singletonAllocations;

		this.context = context;
		this.resolvingContext = resolvingContext;

		this.constructorChain = constructorChain;
		this.dependency = dependency;
		this.dependencyConstructor = dependencyConstructor;

		this.finalizables = finalizables;
		this.destroyables = destroyables;
	}

	InjectionChain extendBy(TypedBlueprint<?> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>(this.typeAllocations);
		Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>(this.singletonAllocations);

		typeAllocations.putAll(blueprint.getTypeAllocations());
		singletonAllocations.putAll(blueprint.getSingletonAllocations());
		ResolvingContext resolvingContext = this.resolvingContext.merge(blueprint.getPropertyAllocations());

		return new InjectionChain(typeAllocations, singletonAllocations, this.context, resolvingContext,
				this.constructorChain, this.dependency, this.dependencyConstructor, this.finalizables,
				this.destroyables);
	}

	InjectionChain extendBy(List<Blueprint> extensions) {
		Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
		Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>();
		ResolvingContext resolvingContext = new ResolvingContext();

		for (Blueprint extension : extensions) {
			typeAllocations.putAll(extension.getTypeAllocations());
			singletonAllocations.putAll(extension.getSingletonAllocations());
			resolvingContext = resolvingContext.merge(extension.getPropertyAllocations());
		}

		typeAllocations.putAll(this.typeAllocations);
		singletonAllocations.putAll(this.singletonAllocations);
		resolvingContext = resolvingContext.merge(this.resolvingContext);

		return new InjectionChain(typeAllocations, singletonAllocations, this.context, resolvingContext,
				this.constructorChain, this.dependency, this.dependencyConstructor, this.finalizables,
				this.destroyables);
	}

	InjectionChain extendBy(Constructor<?> c, boolean isIndependent, SingletonMode mode, String singletonId) {
		DependencyContext dependency = DependencyContext.of(isIndependent, mode);
		Constructor<?> dependencyConstructor = this.dependencyConstructor;
		if (this.dependency.ordinal() > dependency.ordinal()) {
			dependency = this.dependency;
		} else {
			dependencyConstructor = c;
		}

		LinkedHashSet<Constructor<?>> constructorChain = new LinkedHashSet<>(this.constructorChain);
		constructorChain.add(c);

		return new InjectionChain(this.typeAllocations, this.singletonAllocations, this.context, this.resolvingContext,
				constructorChain, dependency, dependencyConstructor, this.finalizables, this.destroyables);
	}

	static InjectionChain forInjection(Map<Type, AbstractAllocator<?>> typeAllocations, InjectionContext baseContext,
			ResolvingContext resolvingContext) {
		return new InjectionChain(typeAllocations, new HashMap<>(), baseContext, resolvingContext);
	}

	static InjectionChain forGlobalSingletonInjection(ResolvingContext resolvingContext) {
		return new InjectionChain(new HashMap<>(), new HashMap<>(), new InjectionContext(resolvingContext), resolvingContext);
	}

	static InjectionChain forSingletonResolving(Map<String, AbstractAllocator<?>> singletonAllocations, InjectionContext baseContext,
			ResolvingContext resolvingContext) {
		return new InjectionChain(new HashMap<>(), singletonAllocations, baseContext, resolvingContext);
	}

	// Blueprint
	boolean hasTypeAllocator(Type type) {
		return this.typeAllocations.containsKey(type);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getTypeAllocator(Type type) {
		return (AbstractAllocator<T>) this.typeAllocations.get(type);
	}

	boolean hasSingletonAllocator(String singletonId) {
		return this.singletonAllocations.containsKey(singletonId);
	}

	@SuppressWarnings("unchecked")
	<T> AbstractAllocator<T> getSingletonAllocator(String singletonId) {
		return (AbstractAllocator<T>) this.singletonAllocations.get(singletonId);
	}

	// Context
	InjectionContext getContext() {
		return this.context;
	}

	boolean hasSingleton(String singletonId) {
		return this.context.hasSingleton(singletonId);
	}

	<T> void addSingleton(String singletonId, T instance) {
		this.context.addSingleton(singletonId, instance);
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