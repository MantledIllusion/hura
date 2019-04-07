package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.annotation.property.Property;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

class AggregateValidator implements AnnotationProcessor<Aggregate, AnnotatedElement> {

	@Construct
    AggregateValidator() {}

	@Override
	public void process(Phase phase, Object bean, Aggregate annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
		if (annotatedElement.isAnnotationPresent(Inject.class)) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
							+ Aggregate.class.getSimpleName() + ", but is also annotated with @"
							+ Inject.class.getSimpleName() + ", which is not allowed.");
		} else if (annotatedElement.isAnnotationPresent(Plugin.class)) {
			throw new ValidatorException(
					"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
							+ Aggregate.class.getSimpleName() + ", but is also annotated with @"
							+ Plugin.class.getSimpleName() + ", which is not allowed.");
		} else {
			Field field = (Field) annotatedElement;
			if (Modifier.isStatic(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Aggregate.class.getSimpleName() + ", but is static, which is not allowed.");
			} else if (Modifier.isFinal(field.getModifiers())) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Aggregate.class.getSimpleName() + ", but is final, which is not allowed.");
			} else if (Collection.class.isAssignableFrom(field.getType())
					&& field.getType() != Collection.class
					&& field.getType() != List.class
					&& field.getType() != Set.class) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Aggregate.class.getSimpleName() + ", but its type requires a "
								+ Collection.class.getSimpleName() + " that is neither " + List.class.getSimpleName()
								+ ", " + Set.class.getSimpleName() + " nor " + Collection.class.getSimpleName()
								+ " itself.");
			}

			if (!StringUtils.isEmpty(annotationInstance.qualifierMatcher())) {
				try {
					Pattern.compile(annotationInstance.qualifierMatcher());
				} catch (PatternSyntaxException | NullPointerException e) {
					throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
							+ " is annotated with @" + Aggregate.class.getSimpleName() + ", but the qualifierMatcher '"
							+ annotationInstance.qualifierMatcher() + "' is no valid pattern.", e);
				}
			}
		}
	}
}
