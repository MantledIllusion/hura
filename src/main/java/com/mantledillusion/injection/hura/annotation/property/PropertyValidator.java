package com.mantledillusion.injection.hura.annotation.property;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class PropertyValidator implements AnnotationProcessor<Property, AnnotatedElement> {

	@Construct
	PropertyValidator() {}

	@Override
	public void process(Phase phase, Object bean, Property annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		Type genericType;
		if (annotatedElement instanceof Field) {
			Field annotatedField = (Field) annotatedElement;

			genericType = annotatedField.getGenericType();

			if (Modifier.isStatic(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Property.class.getSimpleName() + ", but the field is static.");
			} else if (Modifier.isFinal(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Property.class.getSimpleName() + ", but the field is final.");
			}
		} else {
			genericType = ((Parameter) annotatedElement).getParameterizedType();
		}

		if (!TypeUtils.isAssignable(String.class, genericType)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName()
					+ ", but the fields type is not assignable by an instance of String.");
		} else if (StringUtils.isEmpty(annotationInstance.value())) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName() + ", but the property key '"
					+ annotationInstance.value() + "' is empty, which is not allowed.");
		}
	}
}
