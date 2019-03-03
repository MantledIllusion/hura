package com.mantledillusion.injection.hura.annotation.instruction;

import java.lang.reflect.Constructor;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.InjectionUtils;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class ConstructValidator implements AnnotationProcessor<Construct, Constructor<?>> {

	public ConstructValidator() {}

	@Override
	public void process(Phase phase, Object bean, Construct annotationInstance, Constructor<?> c, TemporalInjectorCallback callback) throws Exception {
		if (!InjectionUtils.hasAllParametersDefinable(c)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(c) + " annotated with "
					+ Construct.class.getSimpleName() + " has one or more parameters that do not use the "
					+ Inject.class.getSimpleName() + " annotation; Those parameters cannot be resolved.");
		}
	}
}
