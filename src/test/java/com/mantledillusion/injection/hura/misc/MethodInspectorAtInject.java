package com.mantledillusion.injection.hura.misc;

import java.lang.reflect.Method;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;

public class MethodInspectorAtInject implements Inspector<PostProcessingMethodAnnotationAtInject, Method> {

	@Override
	public void inspect(Object bean, PostProcessingMethodAnnotationAtInject annotationInstance,
			Method annotatedElement, TemporalInjectorCallback callback) throws Exception {
		InjectableWithInspectedAnnotations postProcessable = (InjectableWithInspectedAnnotations) bean;
		postProcessable.elementAtInject = annotatedElement;
		postProcessable.injectableAtInject = postProcessable.injectable;
		postProcessable.occurredPhases.add(Phase.INJECT);
	}
}
