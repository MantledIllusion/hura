package com.mantledillusion.injection.hura.core.annotation;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;

import java.lang.reflect.*;

/**
 * Static util for {@link AnnotationProcessor}s.
 */
public class ValidatorUtils {

	private ValidatorUtils() {}

	/**
	 * Creates a readable description for annotated elements.
	 *
	 * @param annotatedElement The annotatedElement to describe; might be null.
	 * @return A readable description, never null, might be empty if the given annotated element was null
	 */
	public static String getDescription(AnnotatedElement annotatedElement) {
		StringBuilder sb = new StringBuilder();
		if (annotatedElement != null) {
			sb.append(annotatedElement.getClass().getSimpleName().toLowerCase()).append(" '");
			if (annotatedElement instanceof Field) {
				Field annotatedField = (Field) annotatedElement;
				sb.append(annotatedField.getName()).append("' in the type '")
						.append(annotatedField.getDeclaringClass().getSimpleName()).append('\'');
			} else if (annotatedElement instanceof Parameter) {
				Parameter annotatedParameter = (Parameter) annotatedElement;
				sb.append(annotatedParameter.getName()).append("' of the method '")
						.append(annotatedParameter.getDeclaringExecutable().getName()).append("' in the type '")
						.append(annotatedParameter.getDeclaringExecutable().getDeclaringClass().getSimpleName())
						.append('\'');
			} else if (annotatedElement instanceof Constructor) {
				Constructor<?> annotatedConstructor = (Constructor<?>) annotatedElement;
				sb.append(annotatedConstructor.toString()).append('\'');
			} else if (annotatedElement instanceof Method) {
				Method annotatedMethod = (Method) annotatedElement;
				sb.append(annotatedMethod.getName()).append("' in the type '")
						.append(annotatedMethod.getDeclaringClass().getSimpleName()).append('\'');
			}
		}
		return sb.toString();
	}
}
