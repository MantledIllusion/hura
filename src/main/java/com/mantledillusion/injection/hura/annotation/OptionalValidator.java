package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.InjectionException;

class OptionalValidator implements AnnotationValidator<Optional, AnnotatedElement> {

	@Override
	public void validate(Optional annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)
				&& !annotatedElement.isAnnotationPresent(Property.class)) {
			throw new InjectionException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is not annotated with @"
							+ Inject.class.getSimpleName() + " or @" + Property.class.getSimpleName()
							+ ", which it has to be to be annotated with @" + Optional.class.getSimpleName());
		}
	}
}
