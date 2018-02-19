package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;

public class TypeInspectorAtInspect implements Inspector<PostProcessingTypeAnnotationAtInspect, Class<?>> {

	@Override
	public void inspect(Object bean, PostProcessingTypeAnnotationAtInspect annotationInstance,
			Class<?> annotatedElement, TemporalInjectorCallback callback) throws Exception {
		InjectableWithInspectedAnnotations postProcessable = (InjectableWithInspectedAnnotations) bean;
		postProcessable.elementAtInspect = annotatedElement;
		postProcessable.injectableAtInspect = postProcessable.injectable;
		postProcessable.occurredPhases.add(Phase.INSPECT);
	}
}
