package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.exception.ValidatorException;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class PluginValidator implements AnnotationProcessor<Plugin, AnnotatedElement> {

	@Construct
    PluginValidator() {}

	@Override
	public void process(Phase phase, Object bean, Plugin annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (annotatedElement.isAnnotationPresent(Inject.class)) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
							+ Plugin.class.getSimpleName() + ", but is also annotated with @"
							+ Inject.class.getSimpleName() + ", which is not allowed.");
		} else if (!new File(annotationInstance.directory()).isDirectory()) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
							+ Plugin.class.getSimpleName() + ", but the given directory '"
							+ annotationInstance.directory() + "' is no valid directory.");
		} else if (annotatedElement instanceof Field) {
			Field field = (Field) annotatedElement;
			if (Modifier.isStatic(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Plugin.class.getSimpleName() + ", but is static, which is not allowed.");
			} else if (Modifier.isFinal(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Plugin.class.getSimpleName() + ", but is final, which is not allowed.");
			}
		}
	}
}
