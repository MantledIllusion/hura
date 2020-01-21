package com.mantledillusion.injection.hura.core.annotation.injection;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class InjectValidator implements AnnotationProcessor<Inject, AnnotatedElement> {

	@Construct
	InjectValidator() {}

	@Override
	public void process(Phase phase, Object bean, Inject annotationInstance, AnnotatedElement annotatedElement,
						Injector.TemporalInjectorCallback callback) throws Exception {
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
