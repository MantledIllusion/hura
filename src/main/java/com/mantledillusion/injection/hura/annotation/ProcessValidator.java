package com.mantledillusion.injection.hura.annotation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.AnnotationValidator;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.exception.ProcessorException;

class ProcessValidator implements AnnotationValidator<Process, Method> {

	private static final Set<Phase> NO_CALLBACK_PHASES = EnumSet.of(Phase.FINALIZE, Phase.DESTROY);

	@Override
	public void validate(Process annotationInstance, Method m) throws Exception {
		if (Modifier.isStatic(m.getModifiers())) {
			throw new ProcessorException("The method '" + m + "' is annotated with @" + Process.class.getSimpleName()
					+ " but is declared static, which is not allowed.");
		} else if (m.getParameterCount() == 1) {
			if (!TypeUtils.isAssignable(TemporalInjectorCallback.class, m.getGenericParameterTypes()[0])) {
				throw new ProcessorException(
						"The method '" + m + "' is annotated with @" + Process.class.getSimpleName()
								+ " and requires 1 parameter, but this parameter is no assignable for "
								+ TemporalInjectorCallback.class.getSimpleName());
			} else if (NO_CALLBACK_PHASES.contains(annotationInstance.value())) {
				throw new ProcessorException("The method '" + m + "' is annotated with @"
						+ Process.class.getSimpleName() + " and requires a "
						+ TemporalInjectorCallback.class.getSimpleName() + " as parameter, but in its "
						+ Phase.class.getSimpleName() + " '" + annotationInstance.value().name() + "' no "
						+ TemporalInjectorCallback.class.getSimpleName() + " is accessible.");
			}
		} else if (m.getParameterCount() > 1) {
			throw new ProcessorException("The method '" + m + "' is annotated with @" + Process.class.getSimpleName()
					+ " but requires " + m.getParameterCount() + " parameters, which is not allowed.");
		}
	}
}
