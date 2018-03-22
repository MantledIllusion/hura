package com.mantledillusion.injection.hura;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mantledillusion.essentials.reflection.AnnotationEssentials.AnnotationOccurrence;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inspected;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.annotation.Processed;
import com.mantledillusion.injection.hura.exception.ProcessorException;

class InjectionProcessors<T> {

	@SuppressWarnings("rawtypes")
	static final InjectionProcessors EMPTY = of();

	private final Map<Processor.Phase, List<Processor<? super T>>> postProcessors;

	private InjectionProcessors(Map<Processor.Phase, List<Processor<? super T>>> postProcessors) {
		this.postProcessors = postProcessors;
	}

	List<Processor<? super T>> getPostProcessorsOfPhase(Phase phase) {
		return this.postProcessors.get(phase);
	}

	InjectionProcessors<T> merge(InjectionProcessors<T> other) {
		Map<Processor.Phase, List<Processor<? super T>>> postProcessors = new HashMap<>();

		List<Processor<? super T>> inspectProcessors = new ArrayList<>();
		List<Processor<? super T>> constructProcessors = new ArrayList<>();
		List<Processor<? super T>> injectProcessors = new ArrayList<>();
		List<Processor<? super T>> finalizers = new ArrayList<>();
		List<Processor<? super T>> preDestroyers = new ArrayList<>();

		for (InjectionProcessors<T> applicator : Arrays.asList(this, other)) {
			inspectProcessors.addAll(applicator.postProcessors.get(Phase.INSPECT));
			constructProcessors.addAll(applicator.postProcessors.get(Phase.CONSTRUCT));
			injectProcessors.addAll(applicator.postProcessors.get(Phase.INJECT));
			finalizers.addAll(applicator.postProcessors.get(Phase.FINALIZE));
			preDestroyers.addAll(applicator.postProcessors.get(Phase.DESTROY));
		}

		postProcessors.put(Phase.INSPECT, Collections.unmodifiableList(inspectProcessors));
		postProcessors.put(Phase.CONSTRUCT, Collections.unmodifiableList(constructProcessors));
		postProcessors.put(Phase.INJECT, Collections.unmodifiableList(injectProcessors));
		postProcessors.put(Phase.FINALIZE, Collections.unmodifiableList(finalizers));
		postProcessors.put(Phase.DESTROY, Collections.unmodifiableList(preDestroyers));

		return new InjectionProcessors<>(postProcessors);
	}

	@SafeVarargs
	static <T> InjectionProcessors<T> of(PhasedProcessor<? super T>... applicators) {
		Map<Processor.Phase, List<Processor<? super T>>> postProcessors = new HashMap<>();

		List<Processor<? super T>> inspectProcessors = new ArrayList<>();
		List<Processor<? super T>> constructProcessors = new ArrayList<>();
		List<Processor<? super T>> injectProcessors = new ArrayList<>();
		List<Processor<? super T>> finalizers = new ArrayList<>();
		List<Processor<? super T>> preDestroyers = new ArrayList<>();

		if (applicators != null) {
			for (PhasedProcessor<? super T> applicator : applicators) {
				if (applicator != null) {
					switch (applicator.getPhase()) {
					case INSPECT:
						inspectProcessors.add(applicator.getPostProcessor());
						break;
					case CONSTRUCT:
						constructProcessors.add(applicator.getPostProcessor());
						break;
					case INJECT:
						injectProcessors.add(applicator.getPostProcessor());
						break;
					case FINALIZE:
						finalizers.add(applicator.getPostProcessor());
						break;
					case DESTROY:
						preDestroyers.add(applicator.getPostProcessor());
						break;
					}
				}
			}
		}

		postProcessors.put(Phase.INSPECT, Collections.unmodifiableList(inspectProcessors));
		postProcessors.put(Phase.CONSTRUCT, Collections.unmodifiableList(constructProcessors));
		postProcessors.put(Phase.INJECT, Collections.unmodifiableList(injectProcessors));
		postProcessors.put(Phase.FINALIZE, Collections.unmodifiableList(finalizers));
		postProcessors.put(Phase.DESTROY, Collections.unmodifiableList(preDestroyers));

		return new InjectionProcessors<>(postProcessors);
	}

	static <T> InjectionProcessors<T> of(Class<T> clazz, TemporalInjectorCallback callback) {
		Map<Processor.Phase, List<Processor<? super T>>> postProcessors = new HashMap<>();

		List<Processor<? super T>> inspectProcessors = new ArrayList<>();
		List<Processor<? super T>> constructProcessors = new ArrayList<>();
		List<Processor<? super T>> injectProcessors = new ArrayList<>();
		List<Processor<? super T>> finalizers = new ArrayList<>();
		List<Processor<? super T>> preDestroyers = new ArrayList<>();

		// PROCESSING

		for (AnnotationOccurrence annotationEntry : ReflectionCache.getAnnotationsAnnotatedWith(clazz,
				Inspected.class)) {
			Inspected inspected = annotationEntry.getAnnotation().annotationType().getAnnotation(Inspected.class);

			Processor<T> postProcessor = toPostProcessor(inspected, annotationEntry.getAnnotation(),
					annotationEntry.getAnnotatedElement(), callback);

			switch (inspected.phase()) {
			case INSPECT:
				inspectProcessors.add(postProcessor);
				break;
			case CONSTRUCT:
				constructProcessors.add(postProcessor);
				break;
			case INJECT:
				injectProcessors.add(postProcessor);
				break;
			case FINALIZE:
				finalizers.add(postProcessor);
				break;
			case DESTROY:
				preDestroyers.add(postProcessor);
				break;
			}
		}

		for (Class<?> type : ReflectionCache.getSuperTypesAnnotatedWith(clazz, Processed.class)) {
			Processed a = type.getAnnotation(Processed.class);
			for (Processed.PhasedProcessor processor : a.value()) {

				@SuppressWarnings("unchecked")
				Processor<T> postProcessor = (Processor<T>) callback.instantiate(processor.value());

				switch (processor.phase()) {
				case INSPECT:
					inspectProcessors.add(postProcessor);
					break;
				case CONSTRUCT:
					constructProcessors.add(postProcessor);
					break;
				case INJECT:
					injectProcessors.add(postProcessor);
					break;
				case FINALIZE:
					finalizers.add(postProcessor);
					break;
				case DESTROY:
					preDestroyers.add(postProcessor);
					break;
				}
			}
		}

		for (Method m : ReflectionCache.getMethodsAnnotatedWith(clazz, Process.class)) {
			if (!m.isAccessible()) {
				try {
					m.setAccessible(true);
				} catch (SecurityException e) {
					throw new ProcessorException("Unable to gain access to the method '" + m + "' of the type "
							+ clazz.getSimpleName() + " which is inaccessible.", e);
				}
			}

			Processor<T> postProcessor;
			if (m.getParameterCount() == 0) {
				postProcessor = (bean, tCallback) -> {
					try {
						m.invoke(bean);
					} catch (InvocationTargetException e) {
						throw new ProcessorException("Unable to invoke method '" + m.getName() + "' for processing",
								e.getTargetException());
					}
				};
			} else {
				postProcessor = (bean, tCallback) -> {
					try {
						m.invoke(bean, tCallback);
					} catch (InvocationTargetException e) {
						throw new ProcessorException("Unable to invoke method '" + m.getName() + "' for processing",
								e.getTargetException());
					}
				};
			}

			switch (m.getAnnotation(Process.class).value()) {
			case INSPECT:
				inspectProcessors.add(postProcessor);
				break;
			case CONSTRUCT:
				constructProcessors.add(postProcessor);
				break;
			case INJECT:
				injectProcessors.add(postProcessor);
				break;
			case FINALIZE:
				finalizers.add(postProcessor);
				break;
			case DESTROY:
				preDestroyers.add(postProcessor);
				break;
			}
		}

		// DESTRUCTION

		postProcessors.put(Phase.INSPECT, Collections.unmodifiableList(inspectProcessors));
		postProcessors.put(Phase.CONSTRUCT, Collections.unmodifiableList(constructProcessors));
		postProcessors.put(Phase.INJECT, Collections.unmodifiableList(injectProcessors));
		postProcessors.put(Phase.FINALIZE, Collections.unmodifiableList(finalizers));
		postProcessors.put(Phase.DESTROY, Collections.unmodifiableList(preDestroyers));

		return new InjectionProcessors<>(postProcessors);
	}

	private static <A extends Annotation, E extends AnnotatedElement, T> Processor<T> toPostProcessor(
			Inspected inspected, A annotationInstance, E annotatedElement, TemporalInjectorCallback callback) {
		@SuppressWarnings("unchecked")
		Inspector<A, E> inspector = (Inspector<A, E>) callback.instantiate(inspected.value());
		return (bean, beanCallback) -> inspector.inspect(bean, annotationInstance, annotatedElement, beanCallback);
	}
}
