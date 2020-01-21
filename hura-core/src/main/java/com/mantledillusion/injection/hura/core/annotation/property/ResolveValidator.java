package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.*;

class ResolveValidator implements AnnotationProcessor<Resolve, AnnotatedElement> {

	@Construct
    ResolveValidator() {}

	@Override
	public void process(Phase phase, Object bean, Resolve annotationInstance, AnnotatedElement annotatedElement,
						Injector.TemporalInjectorCallback callback) throws Exception {
		Type genericType;
		if (annotatedElement instanceof Field) {
			Field annotatedField = (Field) annotatedElement;

			genericType = annotatedField.getGenericType();

			if (Modifier.isStatic(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Resolve.class.getSimpleName() + ", but the field is static.");
			} else if (Modifier.isFinal(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Resolve.class.getSimpleName() + ", but the field is final.");
			}
		} else {
			genericType = ((Parameter) annotatedElement).getParameterizedType();
		}

		if (!TypeUtils.isAssignable(String.class, genericType)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Resolve.class.getSimpleName()
					+ ", but the fields type is not assignable by an instance of String.");
		} else if (StringUtils.isEmpty(annotationInstance.value())) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Resolve.class.getSimpleName() + ", but the property key '"
					+ annotationInstance.value() + "' is empty, which is not allowed.");
		}
	}
}
