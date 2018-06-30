package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.InjectionException;

class DefaultValueValidator implements AnnotationValidator<DefaultValue, AnnotatedElement> {

	@Override
	public void validate(DefaultValue annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Property.class)) {
			throw new InjectionException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is not annotated with @" + Property.class.getSimpleName()
					+ ", which it has to be to be annotated with @" + DefaultValue.class.getSimpleName());
		} else if (annotatedElement.isAnnotationPresent(Optional.class)) {
			throw new InjectionException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName() + " but also with @"
					+ Optional.class.getSimpleName() + ", which is not allowed when its also annotated with @"
					+ DefaultValue.class.getSimpleName());
		}
	}
}
