package com.mantledillusion.injection.hura.core.annotation.instruction;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.annotation.property.Property;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;

class OptionalValidator implements AnnotationProcessor<Optional, AnnotatedElement> {

	@Construct
	OptionalValidator() {}

	@Override
	public void process(Phase phase, Object bean, Optional annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)
				&& !annotatedElement.isAnnotationPresent(Plugin.class)
				&& !annotatedElement.isAnnotationPresent(Aggregate.class)
				&& !annotatedElement.isAnnotationPresent(Property.class)) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is not annotated with @"
							+ Inject.class.getSimpleName() + ", @" + Plugin.class.getSimpleName() + ", @"
							+ Aggregate.class.getSimpleName() + " or @" + Property.class.getSimpleName()
							+ ", which it has to be to be annotated with @" + Optional.class.getSimpleName());
		}
	}
}
