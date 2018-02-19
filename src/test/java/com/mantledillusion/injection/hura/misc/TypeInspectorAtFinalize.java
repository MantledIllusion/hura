package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;

public class TypeInspectorAtFinalize implements Inspector<PostProcessingTypeAnnotationAtFinalize, Class<?>> {

	@Override
	public void inspect(Object bean, PostProcessingTypeAnnotationAtFinalize annotationInstance,
			Class<?> annotatedElement, TemporalInjectorCallback callback) throws Exception {
		InjectableWithInspectedAnnotations postProcessable = (InjectableWithInspectedAnnotations) bean;
		postProcessable.elementAtFinalize = annotatedElement;
		postProcessable.injectableAtFinalize = postProcessable.injectable;
		postProcessable.occurredPhases.add(Phase.FINALIZE);
	}
}
