package com.mantledillusion.injection.hura.injectables;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.misc.PostProcessingFieldAnnotationAtConstruct;
import com.mantledillusion.injection.hura.misc.PostProcessingMethodAnnotationAtInject;
import com.mantledillusion.injection.hura.misc.PostProcessingTypeAnnotationAtDestroy;
import com.mantledillusion.injection.hura.misc.PostProcessingTypeAnnotationAtFinalize;
import com.mantledillusion.injection.hura.misc.PostProcessingTypeAnnotationAtInspect;

@PostProcessingTypeAnnotationAtInspect
@PostProcessingTypeAnnotationAtDestroy
@PostProcessingTypeAnnotationAtFinalize
public class InjectableWithInspectedAnnotations extends InjectableWithProcessableFields {

	public AnnotatedElement elementAtInspect;
	public AnnotatedElement elementAtConstruct;
	public AnnotatedElement elementAtInject;
	public AnnotatedElement elementAtDestroy;
	public AnnotatedElement elementAtFinalize;
	
	@PostProcessingFieldAnnotationAtConstruct
	private String inspectedField;
	
	@PostProcessingMethodAnnotationAtInject
	private String inspectedMethod() {
		return null;
	}
}
