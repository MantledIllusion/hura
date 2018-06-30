package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class PropertyValidator implements AnnotationValidator<Property, AnnotatedElement> {

	@Override
	public void validate(Property annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		Type genericType;
		String annotatedElementName;
		if (annotatedElement instanceof Field) {
			Field annotatedField = (Field) annotatedElement;

			genericType = annotatedField.getGenericType();
			annotatedElementName = "Field '" + annotatedField.getName() + "' in the type '"
					+ annotatedField.getDeclaringClass().getSimpleName() + "'";

			if (Modifier.isStatic(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + annotatedElementName + " is annotated with @"
						+ Property.class.getSimpleName() + ", but the field is static.");
			} else if (Modifier.isFinal(annotatedField.getModifiers())) {
				throw new ValidatorException("The " + annotatedElementName + " is annotated with @"
						+ Property.class.getSimpleName() + ", but the field is final.");
			}
		} else {
			genericType = ((Parameter) annotatedElement).getParameterizedType();
			annotatedElementName = "Parameter '" + ((Parameter) annotatedElement).getName() + "' in the executable '"
					+ ((Parameter) annotatedElement).getDeclaringExecutable().getName() + "'";
		}

		if (!TypeUtils.isAssignable(String.class, genericType)) {
			throw new ValidatorException(
					"The " + annotatedElementName + " is annotated with @" + Property.class.getSimpleName()
							+ ", but the fields type is not assignable by an instance of String.");
		} else if (StringUtils.isEmpty(annotationInstance.value())) {
			throw new ValidatorException("The " + annotatedElementName + " is annotated with @"
					+ Property.class.getSimpleName() + ", but the property key '" + annotationInstance.value()
					+ "' is empty, which is not allowed.");
		}
	}
}
