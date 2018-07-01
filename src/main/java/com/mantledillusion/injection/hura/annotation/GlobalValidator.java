package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class GlobalValidator implements AnnotationValidator<Global, AnnotatedElement> {

	@Override
	public void validate(Global annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement) + " is not annotated with @"
					+ Inject.class.getSimpleName() + ", which it has to be to be annotated with @"
					+ Global.class.getSimpleName());
		} else if (annotatedElement.getAnnotation(Inject.class).value().isEmpty()) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
					+ Inject.class.getSimpleName()
					+ " without using a qualifier to define it as a singleton, which it has to be to be annotated with @"
					+ Global.class.getSimpleName());
		}
	}
}
