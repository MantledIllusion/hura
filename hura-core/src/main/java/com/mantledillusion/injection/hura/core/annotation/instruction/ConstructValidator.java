package com.mantledillusion.injection.hura.core.annotation.instruction;

import com.mantledillusion.injection.hura.core.InjectionUtils;
import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.Constructor;

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
