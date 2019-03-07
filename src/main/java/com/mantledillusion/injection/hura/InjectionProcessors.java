package com.mantledillusion.injection.hura;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;

import com.mantledillusion.essentials.reflection.AnnotationEssentials.AnnotationOccurrence;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.exception.ProcessorException;

final class InjectionProcessors<T> {

	interface LifecycleAnnotationProcessor<T> {

		void process(T bean, TemporalInjectorCallback callback) throws Exception;
	}

	private final Map<Phase, List<LifecycleAnnotationProcessor<? super T>>> processors;

	private InjectionProcessors(Map<Phase, List<LifecycleAnnotationProcessor<? super T>>> processors) {
		this.processors = processors;
	}

	List<LifecycleAnnotationProcessor<? super T>> getProcessorsOfPhase(Phase phase) {
		return this.processors.get(phase);
	}

	InjectionProcessors<T> merge(InjectionProcessors<T> other) {
		Map<Phase, List<LifecycleAnnotationProcessor<? super T>>> processors = new HashMap<>();

		List<LifecycleAnnotationProcessor<? super T>> preConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postInjectProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> preDestroyProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postDestroyProcessors = new ArrayList<>();

		for (InjectionProcessors<T> applicator : Arrays.asList(this, other)) {
			preConstructProcessors.addAll(applicator.processors.get(Phase.PRE_CONSTRUCT));
			postInjectProcessors.addAll(applicator.processors.get(Phase.POST_INJECT));
			postConstructProcessors.addAll(applicator.processors.get(Phase.POST_CONSTRUCT));
			preDestroyProcessors.addAll(applicator.processors.get(Phase.PRE_DESTROY));
			postDestroyProcessors.addAll(applicator.processors.get(Phase.POST_DESTROY));
		}

		processors.put(Phase.PRE_CONSTRUCT, Collections.unmodifiableList(preConstructProcessors));
		processors.put(Phase.POST_INJECT, Collections.unmodifiableList(postInjectProcessors));
		processors.put(Phase.POST_CONSTRUCT, Collections.unmodifiableList(postConstructProcessors));
		processors.put(Phase.PRE_DESTROY, Collections.unmodifiableList(preDestroyProcessors));
		processors.put(Phase.POST_DESTROY, Collections.unmodifiableList(postDestroyProcessors));

		return new InjectionProcessors<>(processors);
	}

	@SafeVarargs
	static <T> InjectionProcessors<T> of(PhasedBeanProcessor<? super T>... applicators) {
		Map<Phase, List<LifecycleAnnotationProcessor<? super T>>> processors = new HashMap<>();

		List<LifecycleAnnotationProcessor<? super T>> preConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postInjectProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> preDestroyProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postDestroyProcessors = new ArrayList<>();

		if (applicators != null) {
			for (PhasedBeanProcessor<? super T> applicator : applicators) {
				if (applicator != null) {
					LifecycleAnnotationProcessor<T> annotationProcessor = (bean, tCallback) ->
							applicator.getPostProcessor().process(applicator.getPhase(), bean, tCallback);
					switch (applicator.getPhase()) {
						case PRE_CONSTRUCT:
							preConstructProcessors.add(annotationProcessor);
							break;
						case POST_INJECT:
							postInjectProcessors.add(annotationProcessor);
							break;
						case POST_CONSTRUCT:
							postConstructProcessors.add(annotationProcessor);
							break;
						case PRE_DESTROY:
							preDestroyProcessors.add(annotationProcessor);
							break;
						case POST_DESTROY:
							postDestroyProcessors.add(annotationProcessor);
							break;
					}
				}
			}
		}

		processors.put(Phase.PRE_CONSTRUCT, Collections.unmodifiableList(preConstructProcessors));
		processors.put(Phase.POST_INJECT, Collections.unmodifiableList(postInjectProcessors));
		processors.put(Phase.POST_CONSTRUCT, Collections.unmodifiableList(postConstructProcessors));
		processors.put(Phase.PRE_DESTROY, Collections.unmodifiableList(preDestroyProcessors));
		processors.put(Phase.POST_DESTROY, Collections.unmodifiableList(postDestroyProcessors));

		return new InjectionProcessors<>(processors);
	}

	static <T> InjectionProcessors<T> of(Class<T> clazz, TemporalInjectorCallback callback) {
		Map<Phase, List<LifecycleAnnotationProcessor<? super T>>> processors = new HashMap<>();

		List<LifecycleAnnotationProcessor<? super T>> preConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postInjectProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postConstructProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> preDestroyProcessors = new ArrayList<>();
		List<LifecycleAnnotationProcessor<? super T>> postDestroyProcessors = new ArrayList<>();

		addBeanProcessorsFromAnnotation(Phase.PRE_CONSTRUCT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreConstruct.class, a -> a.value(), preConstructProcessors);
		addAnnotationProcessorsFromAnnotation(Phase.PRE_CONSTRUCT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct.class, a -> a.value(), preConstructProcessors);

		addBeanProcessorsFromAnnotation(Phase.POST_INJECT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject.class, a -> a.value(), postInjectProcessors);
		addAnnotationProcessorsFromAnnotation(Phase.POST_INJECT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PostInject.class, a -> a.value(), postInjectProcessors);

		addBeanProcessorsFromAnnotation(Phase.POST_CONSTRUCT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct.class, a -> a.value(), postConstructProcessors);
		addAnnotationProcessorsFromAnnotation(Phase.POST_CONSTRUCT, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PostConstruct.class, a -> a.value(), postConstructProcessors);

		addBeanProcessorsFromAnnotation(Phase.PRE_DESTROY, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreDestroy.class, a -> a.value(), preDestroyProcessors);
		addAnnotationProcessorsFromAnnotation(Phase.PRE_DESTROY, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreDestroy.class, a -> a.value(), preDestroyProcessors);

		addBeanProcessorsFromAnnotation(Phase.POST_DESTROY, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostDestroy.class, a -> a.value(), postDestroyProcessors);
		addAnnotationProcessorsFromAnnotation(Phase.POST_DESTROY, clazz, callback,
				com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PostDestroy.class, a -> a.value(), postDestroyProcessors);

		processors.put(Phase.PRE_CONSTRUCT, Collections.unmodifiableList(preConstructProcessors));
		processors.put(Phase.POST_INJECT, Collections.unmodifiableList(postInjectProcessors));
		processors.put(Phase.POST_CONSTRUCT, Collections.unmodifiableList(postConstructProcessors));
		processors.put(Phase.PRE_DESTROY, Collections.unmodifiableList(preDestroyProcessors));
		processors.put(Phase.POST_DESTROY, Collections.unmodifiableList(postDestroyProcessors));

		return new InjectionProcessors<>(processors);
	}

	private static <T, A extends Annotation> void addBeanProcessorsFromAnnotation(Phase phase, Class<T> clazz, TemporalInjectorCallback callback, Class<A> annotationType,
																				  Function<A, Class<? extends BeanProcessor>[]> processorTypeRetriever,
																				  List<LifecycleAnnotationProcessor<? super T>> processorList) {

		// PROCESSOR ANNOTATIONS ON SUPER CLASSES
		for (Class<?> type : ReflectionCache.getSuperTypesAnnotatedWith(clazz, annotationType)) {
			A a = type.getAnnotation(annotationType);
			for (Class<? extends BeanProcessor> processorType: processorTypeRetriever.apply(a)) {
				BeanProcessor<T> postProcessor = (BeanProcessor<T>) callback.instantiate(processorType);
				processorList.add((bean, tCallback) -> postProcessor.process(phase, bean, tCallback));
			}
		}

		// PROCESSOR ANNOTATIONS ON METHODS
		for (Method m : ReflectionCache.getMethodsAnnotatedWith(clazz, annotationType)) {
			A a = m.getAnnotation(annotationType);
			for (Class<? extends BeanProcessor> processorType: processorTypeRetriever.apply(a)) {
				BeanProcessor<T> postProcessor = (BeanProcessor<T>) callback.instantiate(processorType);
				processorList.add((bean, tCallback) -> postProcessor.process(phase, bean, tCallback));
			}

			if (!m.isAccessible()) {
				try {
					m.setAccessible(true);
				} catch (SecurityException e) {
					throw new ProcessorException("Unable to gain access to the method '" + m + "' of the type "
							+ clazz.getSimpleName() + " which is inaccessible.", e);
				}
			}

			LifecycleAnnotationProcessor<T> processor = (bean, tCallback) -> {
				Object[] parameters = new Object[m.getParameterCount()];

				int parameterIndex=0;
				for (Parameter parameter: m.getParameters()) {
					Object instance = null;
					if (parameter.getType().isAssignableFrom(Phase.class)) {
						instance = phase;
					} else if (parameter.getType().isAssignableFrom(TemporalInjectorCallback.class)) {
						instance = tCallback;
					}
					parameters[parameterIndex] = instance;
					parameterIndex++;
				}

				try {
					m.invoke(bean, parameters);
				} catch (InvocationTargetException e) {
					throw new ProcessorException("Unable to invoke method '" + m.getName() + "' for processing",
							e.getTargetException());
				}
			};

			processorList.add(processor);
		}
	}

	private static <T, LifecycleAnnotationType extends Annotation, AnnotatedAnnotationType extends Annotation, AnnotatedElementType extends AnnotatedElement> void
	addAnnotationProcessorsFromAnnotation(Phase phase, Class<T> clazz, TemporalInjectorCallback callback, Class<LifecycleAnnotationType> annotationType,
										  Function<LifecycleAnnotationType, Class<? extends AnnotationProcessor>[]> processorTypeRetriever,
										  List<LifecycleAnnotationProcessor<? super T>> processorList) {
		// PROCESSOR ANNOTATIONS ON ANNOTATIONS
		for (AnnotationOccurrence occurrence : ReflectionCache.getAnnotationsAnnotatedWith(clazz, annotationType)) {
			LifecycleAnnotationType a = occurrence.getAnnotation().annotationType().getAnnotation(annotationType);
			for (Class<? extends AnnotationProcessor> processorType: processorTypeRetriever.apply(a)) {
				AnnotationProcessor<AnnotatedAnnotationType, AnnotatedElementType> processor = (AnnotationProcessor<AnnotatedAnnotationType, AnnotatedElementType>) callback.instantiate(processorType);
				processorList.add((bean, tCallback) -> processor.process(phase, bean, (AnnotatedAnnotationType) occurrence.getAnnotation(), (AnnotatedElementType) occurrence.getAnnotatedElement(), tCallback));
			}
		}
	}
}