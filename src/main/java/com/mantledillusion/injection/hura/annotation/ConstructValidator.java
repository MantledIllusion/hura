package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.Constructor;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.InjectionUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class ConstructValidator implements AnnotationValidator<Construct, Constructor<?>> {

	@Override
	public void validate(Construct annotationInstance, Constructor<?> c) throws Exception {
		if (!InjectionUtils.hasAllParametersDefinable(c)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(c) + " annotated with "
					+ Construct.class.getSimpleName() + " has one or more parameters that do not use the "
					+ Inject.class.getSimpleName() + " annotation; Those parameters cannot be resolved.");
		}
	}
}
