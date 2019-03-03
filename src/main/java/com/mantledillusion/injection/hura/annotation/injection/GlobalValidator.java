package com.mantledillusion.injection.hura.annotation.injection;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class GlobalValidator implements AnnotationProcessor<Global, AnnotatedElement> {

	@Construct
	GlobalValidator() {}

	@Override
	public void process(Phase phase, Object bean, Global annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
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
