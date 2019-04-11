package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;

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
