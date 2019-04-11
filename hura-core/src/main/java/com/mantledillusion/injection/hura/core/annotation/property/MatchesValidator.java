package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class MatchesValidator implements AnnotationProcessor<Matches, AnnotatedElement> {

	@Construct
	MatchesValidator() {}

	@Override
	public void process(Phase phase, Object bean, Matches annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Property.class)) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is not annotated with @" + Property.class.getSimpleName()
					+ ", which it has to be to be annotated with @" + Matches.class.getSimpleName());
		}

		Pattern pattern;
		try {
			pattern = Pattern.compile(annotationInstance.value());
		} catch (PatternSyntaxException | NullPointerException e) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName() + ", but the matcher '"
					+ annotationInstance.value() + "' is no valid pattern.", e);
		}

		if (annotatedElement.isAnnotationPresent(DefaultValue.class)) {
			DefaultValue defaultValue = annotatedElement.getAnnotation(DefaultValue.class);

			if (!pattern.matcher(defaultValue.value()).matches()) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Property.class.getSimpleName() + ", @"
						+ Matches.class.getSimpleName() + " and @" + DefaultValue.class + ", but the default value '"
						+ defaultValue.value() + "' does not match the specified matcher pattern '"
						+ annotationInstance.value() + "'.");
			}
		}
	}
}
