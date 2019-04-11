package com.mantledillusion.injection.hura.annotation.property;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class DefaultValueValidator implements AnnotationProcessor<DefaultValue, AnnotatedElement> {

	@Construct
	DefaultValueValidator() {}

	@Override
	public void process(Phase phase, Object bean, DefaultValue annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Property.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is not annotated with @" + Property.class.getSimpleName()
					+ ", which it has to be to be annotated with @" + DefaultValue.class.getSimpleName());
		} else if (annotatedElement.isAnnotationPresent(Optional.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName() + " but also with @"
					+ Optional.class.getSimpleName() + ", which is not allowed when its also annotated with @"
					+ DefaultValue.class.getSimpleName());
		}
	}
}
