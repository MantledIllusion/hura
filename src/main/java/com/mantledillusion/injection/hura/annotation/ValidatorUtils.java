package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

class ValidatorUtils {



	static String getDescription(AnnotatedElement annotatedElement) {
		StringBuilder sb = new StringBuilder(annotatedElement.getClass().getSimpleName().toLowerCase()).append(" '");
		if (annotatedElement instanceof Field) {
			Field annotatedField = (Field) annotatedElement;
			sb.append(annotatedField.getName()).append("' in the type '")
					.append(annotatedField.getDeclaringClass().getSimpleName()).append('\'');
		} else {
			Parameter annotatedParameter = (Parameter) annotatedElement;
			sb.append(annotatedParameter.getName()).append("' of the method '")
					.append(annotatedParameter.getDeclaringExecutable().getName()).append("' in the type '")
					.append(annotatedParameter.getDeclaringExecutable().getDeclaringClass().getSimpleName())
					.append('\'');
		}
		return sb.toString();
	}
}
