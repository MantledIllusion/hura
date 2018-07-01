package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;

import javax.xml.bind.ValidationException;

import com.mantledillusion.injection.hura.AnnotationValidator;

class AdjustValidator implements AnnotationValidator<Adjust, AnnotatedElement> {

	@Override
	public void validate(Adjust annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)) {
			throw new ValidationException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Adjust.class.getSimpleName() + ", but is not annotated with @"
					+ Inject.class.getSimpleName() + ", so there is no injection to adjust.");
		} else if (!annotatedElement.getAnnotation(Inject.class).value().isEmpty()) {
			throw new ValidationException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Adjust.class.getSimpleName() + ", the @" + Inject.class.getSimpleName()
					+ " annotation requires a singleton to be injected; singleton injections cannot be customized, "
					+ "as that would allow singletons to be injected differently depending on where they are injected first.");
		}
	}
}
