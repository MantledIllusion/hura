package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;

public class TypeInspectorAtDestroy implements Inspector<PostProcessingTypeAnnotationAtDestroy, Class<?>> {

	@Override
	public void inspect(Object bean, PostProcessingTypeAnnotationAtDestroy annotationInstance,
			Class<?> annotatedElement, TemporalInjectorCallback callback) throws Exception {
		InjectableWithInspectedAnnotations postProcessable = (InjectableWithInspectedAnnotations) bean;
		postProcessable.elementAtDestroy = annotatedElement;
		postProcessable.injectableAtDestroy = postProcessable.injectable;
		postProcessable.occurredPhases.add(Phase.DESTROY);
	}
}
