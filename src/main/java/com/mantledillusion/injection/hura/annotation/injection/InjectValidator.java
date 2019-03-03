package com.mantledillusion.injection.hura.annotation.injection;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class InjectValidator implements AnnotationProcessor<Inject, AnnotatedElement> {

	@Construct
	InjectValidator() {}

	@Override
	public void process(Phase phase, Object bean, Inject annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (annotatedElement instanceof Field) {
			Field field = (Field) annotatedElement;
			if (Modifier.isStatic(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Inject.class.getSimpleName() + ", but is static, which is not allowed.");
			} else if (Modifier.isFinal(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Inject.class.getSimpleName() + ", but is final, which is not allowed.");
			}
		}
	}
}
