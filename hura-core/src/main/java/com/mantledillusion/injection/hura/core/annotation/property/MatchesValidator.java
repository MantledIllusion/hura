package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;

class MatchesValidator implements AnnotationProcessor<Matches, AnnotatedElement> {

	@Construct
	MatchesValidator() {}

	@Override
	public void process(Phase phase, Object bean, Matches annotationInstance, AnnotatedElement annotatedElement,
						Injector.TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Resolve.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is not annotated with @" + Resolve.class.getSimpleName()
					+ ", which it has to be to be annotated with @" + Matches.class.getSimpleName());
		}
	}
}
