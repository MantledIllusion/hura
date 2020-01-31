package com.mantledillusion.injection.hura.core.annotation.injection;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

class AggregateValidator implements AnnotationProcessor<Aggregate, AnnotatedElement> {

	@Construct
    AggregateValidator() {}

	@Override
	public void process(Phase phase, Object bean, Aggregate annotationInstance, AnnotatedElement annotatedElement,
						Injector.TemporalInjectorCallback callback) throws Exception {
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
		} else if (annotatedElement instanceof Field) {
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
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Aggregate.class.getSimpleName() + ", but its type requires a "
						+ Collection.class.getSimpleName() + " that is neither " + List.class.getSimpleName()
						+ ", " + Set.class.getSimpleName() + " nor " + Collection.class.getSimpleName() + " itself.");
			}
		} else if (annotatedElement instanceof Parameter) {
			Parameter parameter = (Parameter) annotatedElement;
			if (parameter.getDeclaringExecutable() instanceof Constructor) {
				throw new ValidatorException("The " + ValidatorUtils.getDescription(annotatedElement)
						+ " is annotated with @" + Aggregate.class.getSimpleName() + ", but its a "
						+ Constructor.class.getSimpleName() + " " + Parameter.class.getSimpleName()
						+ ", which would require beans to be available for aggregation during the "
						+ Phase.PRE_CONSTRUCT.name() + " " + Phase.class.getSimpleName() + ", which they are not.");
			} else if (Collection.class.isAssignableFrom(parameter.getType())
					&& parameter.getType() != Collection.class
					&& parameter.getType() != List.class
					&& parameter.getType() != Set.class) {
				throw new ValidatorException(
						"The " + ValidatorUtils.getDescription(annotatedElement) + " is annotated with @"
								+ Aggregate.class.getSimpleName() + ", but its type requires a "
								+ Collection.class.getSimpleName() + " that is neither " + List.class.getSimpleName()
								+ ", " + Set.class.getSimpleName() + " nor " + Collection.class.getSimpleName()
								+ " itself.");
			}
		}
	}
}
