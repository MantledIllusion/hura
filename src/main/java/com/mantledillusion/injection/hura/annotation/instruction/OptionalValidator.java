package com.mantledillusion.injection.hura.annotation.instruction;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.property.Property;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class OptionalValidator implements AnnotationProcessor<Optional, AnnotatedElement> {

	@Construct
	OptionalValidator() {}

	@Override
	public void process(Phase phase, Object bean, Optional annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)
				&& !annotatedElement.isAnnotationPresent(Plugin.class)
				&& !annotatedElement.isAnnotationPresent(Property.class)) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is not annotated with @"
							+ Inject.class.getSimpleName() + ", @" + Plugin.class.getSimpleName()
							+ " or @" + Property.class.getSimpleName() + ", which it has to be to be annotated with @"
							+ Optional.class.getSimpleName());
		}
	}
}
