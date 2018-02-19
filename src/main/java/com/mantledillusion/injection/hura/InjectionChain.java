package com.mantledillusion.injection.hura;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Injector.AbstractAllocator;
import com.mantledillusion.injection.hura.Injector.SelfSustaningProcessor;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;
import com.mantledillusion.injection.hura.exception.InjectionException;

final class InjectionChain {

	// Root Blueprint
	private final Map<Type, AbstractAllocator<?>> typeAllocations;
	private final Map<String, AbstractAllocator<?>> singletonAllocations;

	// Context
	private final InjectionContext context;
	private final ResolvingContext resolvingContext;

	// Injection Chain
	private final Set<Constructor<?>> constructors;
	private final List<Constructor<?>> constructorChain;

	// Processability
	private final List<SelfSustaningProcessor> finalizables;
	private final List<SelfSustaningProcessor> destroyables;

	private InjectionChain(TypedBlueprint<?> blueprint, InjectionContext baseContext,
			ResolvingContext resolvingContext) {
		this.typeAllocations = blueprint.getTypeAllocations();
		this.singletonAllocations = blueprint.getSingletonAllocations();

		this.context = new InjectionContext(baseContext);
		this.resolvingContext = resolvingContext.merge(blueprint.getPropertyAllocations());

		this.constructors = new HashSet<>();
		this.constructorChain = new ArrayList<>();

		this.finalizables = new ArrayList<>();
		this.destroyables = new ArrayList<>();
	}

	private InjectionChain(Map<Type, AbstractAllocator<?>> typeAllocations,
			Map<String, AbstractAllocator<?>> singletonAllocations, InjectionContext context,
			ResolvingContext resolvingContext, List<Constructor<?>> constructorChain,
			List<SelfSustaningProcessor> finalizables,
			List<SelfSustaningProcessor> destroyables) {
		this.typeAllocations = typeAllocations;
		this.singletonAllocations = singletonAllocations;

		this.context = context;
		this.resolvingContext = resolvingContext;

		this.constructors = new HashSet<>(constructorChain);
		this.constructorChain = constructorChain;

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
				this.constructorChain, this.finalizables, this.destroyables);
	}
	
	InjectionChain extendBy(List<Blueprint> extensions) {
		Map<Type, AbstractAllocator<?>> typeAllocations = new HashMap<>();
		Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>();
		ResolvingContext resolvingContext = new ResolvingContext();
		
		for (Blueprint extension: extensions) {
			typeAllocations.putAll(extension.getTypeAllocations());
			singletonAllocations.putAll(extension.getSingletonAllocations());
			resolvingContext = resolvingContext.merge(extension.getPropertyAllocations());
		}
		
		typeAllocations.putAll(this.typeAllocations);
		singletonAllocations.putAll(this.singletonAllocations);
		resolvingContext = resolvingContext.merge(this.resolvingContext);
		
		return new InjectionChain(typeAllocations, singletonAllocations, this.context, resolvingContext,
				this.constructorChain, this.finalizables, this.destroyables);
	}

	InjectionChain extendBy(Constructor<?> c, boolean isIndependent, SingletonMode mode, String singletonId) {
		if (this.constructors.contains(c)) {
			throw new InjectionException(
					"Injection dependecy cycle detected: " + getStringifiedChainSinceConstructor(c));
		}
		List<SelfSustaningProcessor> finalizables = isIndependent ? this.finalizables : new ArrayList<>();
		List<SelfSustaningProcessor> destroyables = isIndependent ? this.destroyables : new ArrayList<>();
		return new InjectionChain(this.typeAllocations, this.singletonAllocations, this.context, this.resolvingContext,
				ListUtils.union(this.constructorChain, Collections.singletonList(c)), finalizables, destroyables);
	}

	static InjectionChain of(TypedBlueprint<?> blueprint, InjectionContext baseContext,
			ResolvingContext resolvingContext) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}
		return new InjectionChain(blueprint, baseContext, resolvingContext);
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
	boolean hasSingleton(String singletonId) {
		return this.context.hasSingleton(singletonId);
	}

	<T> void addSingleton(String singletonId, T instance, List<SelfSustaningProcessor> destroyables) {
		this.context.addSingleton(singletonId, instance, destroyables);
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
	String getStringifiedChainSinceConstructor(Constructor<?> c) {
		return StringUtils.join(
				this.constructorChain.subList(this.constructorChain.indexOf(c), this.constructorChain.size()), " -> ");
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