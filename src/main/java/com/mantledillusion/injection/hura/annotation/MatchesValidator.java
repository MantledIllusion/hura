package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.exception.ValidatorException;

class MatchesValidator implements AnnotationValidator<Matches, AnnotatedElement> {

	@Override
	public void validate(Matches annotationInstance, AnnotatedElement annotatedElement) throws Exception {
		if (!annotatedElement.isAnnotationPresent(Property.class)) {
			throw new InjectionException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is not annotated with @" + Property.class.getSimpleName()
					+ ", which it has to be to be annotated with @" + Matches.class.getSimpleName());
		}

		Pattern pattern;
		try {
			pattern = Pattern.compile(annotationInstance.value());
		} catch (PatternSyntaxException | NullPointerException e) {
			throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
					+ " is annotated with @" + Property.class.getSimpleName() + ", but the matcher  '"
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
