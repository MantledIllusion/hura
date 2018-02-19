package com.mantledillusion.injection.hura.misc;

import java.lang.reflect.Field;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;

public class FieldInspectorAtConstruct implements Inspector<PostProcessingFieldAnnotationAtConstruct, Field> {

	@Override
	public void inspect(Object bean, PostProcessingFieldAnnotationAtConstruct annotationInstance,
			Field annotatedElement, TemporalInjectorCallback callback) throws Exception {
		InjectableWithInspectedAnnotations postProcessable = (InjectableWithInspectedAnnotations) bean;
		postProcessable.elementAtConstruct = annotatedElement;
		postProcessable.injectableAtConstruct = postProcessable.injectable;
		postProcessable.occurredPhases.add(Phase.CONSTRUCT);
	}
}
