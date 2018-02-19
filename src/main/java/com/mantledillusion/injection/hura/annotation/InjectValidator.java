package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.InjectionException;

class InjectValidator implements AnnotationValidator<Inject, AnnotatedElement> {

	@Override
	public void validate(Inject annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (annotatedElement instanceof Field) {
			Field field = (Field) annotatedElement;
			if (Modifier.isStatic(field.getModifiers())) {
				throw new InjectionException("The field '" + field.getName() + "' in the type '"
						+ field.getDeclaringClass().getSimpleName() + "' is annotated with @"
						+ Inject.class.getSimpleName() + ", but is static, which is not allowed.");
			} else if (Modifier.isFinal(field.getModifiers())) {
				throw new InjectionException("The field '" + field.getName() + "' in the type '"
						+ field.getDeclaringClass().getSimpleName() + "' is annotated with @"
						+ Inject.class.getSimpleName() + ", but is final, which is not allowed.");
			}
		}
	}
}
