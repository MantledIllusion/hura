package com.mantledillusion.injection.hura.annotation.instruction;

import java.lang.reflect.AnnotatedElement;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class AdjustValidator implements AnnotationProcessor<Adjust, AnnotatedElement> {

	@Construct
	AdjustValidator() {}

	@Override
	public void process(Phase phase, Object bean, Adjust annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Inject.class)
				&& !annotatedElement.isAnnotationPresent(Plugin.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Adjust.class.getSimpleName() + ", but is neither annotated with @"
					+ Inject.class.getSimpleName() + " nor with @" + Plugin.class.getSimpleName()
					+ ", so there is no injection to adjust.");
		} else if (annotatedElement.getAnnotation(Qualifier.class) != null) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Adjust.class.getSimpleName() + ", the @" + Qualifier.class.getSimpleName()
					+ " annotation requires a singleton to be injected; singleton injections cannot be customized, "
					+ "as that would allow singletons to be injected differently depending on where they are injected first.");
		}
	}
}
