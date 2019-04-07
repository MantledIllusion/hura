package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;

class QualifierValidator implements AnnotationProcessor<Qualifier, AnnotatedElement> {

	@Construct
    QualifierValidator() {}

	@Override
	public void process(Phase phase, Object bean, Qualifier annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class) && !annotatedElement.isAnnotationPresent(Plugin.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement) + " is neither annotated with @"
					+ Inject.class.getSimpleName() + " nor with @" + Plugin.class.getSimpleName() + ", which it has to be to be annotated with @"
					+ Qualifier.class.getSimpleName());
		}
	}
}
