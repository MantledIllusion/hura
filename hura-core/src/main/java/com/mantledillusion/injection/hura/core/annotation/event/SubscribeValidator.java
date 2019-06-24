package com.mantledillusion.injection.hura.core.annotation.event;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

class SubscribeValidator implements AnnotationProcessor<Subscribe, Method> {

    @Construct
    private SubscribeValidator() {}

    @Override
    public void process(Phase phase, Object bean, Subscribe annotationInstance, Method method, Injector.TemporalInjectorCallback callback) throws Exception {
        Parameter[] parameters = method.getParameters();
        if (Modifier.isStatic(method.getModifiers())) {
            throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                    + Subscribe.class.getSimpleName() + " but is declared static, which is not allowed.");
        } else if (parameters.length > 1) {
            throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                    + Subscribe.class.getSimpleName() + " but declares " + parameters.length + " parameters; "
                    + "subscribing methods might only receive the event or nothing as argument.");
        } else if (parameters.length == 0) {
            if (annotationInstance.value().length == 0) {
                throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                        + Subscribe.class.getSimpleName() + " and declares no parameters, but does also not declare "
                        + "at least one event extension class which is required when not using a parameter.");
            }
        } else if (annotationInstance.value().length > 0) {
            Class<?> parameterType = parameters[0].getType();
            for (Class<?> extensionType: annotationInstance.value()) {
                if (!parameterType.isAssignableFrom(extensionType)) {
                    throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                            + Subscribe.class.getSimpleName() + " and declares a parameter of the type "
                            + parameterType.getSimpleName() + ", but also declares an extension of the type "
                            + extensionType.getSimpleName() + " which is not assignable.");
                }
            }
        }
    }
}
