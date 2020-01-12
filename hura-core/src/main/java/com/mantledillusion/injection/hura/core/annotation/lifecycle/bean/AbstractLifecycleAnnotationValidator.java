package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

abstract class AbstractLifecycleAnnotationValidator<A extends Annotation> implements AnnotationProcessor<A, AnnotatedElement> {

    private final Class<A> annotationType;
    private final boolean allowTemporalInjectorCallbackParameter;
    private final boolean allowInjectParameter;
    private final boolean allowPluginParameter;

    AbstractLifecycleAnnotationValidator(Class<A> annotationType, boolean allowTemporalInjectorCallbackParameter,
                                         boolean allowInjectParameter, boolean allowPluginParameter) {
        this.annotationType = annotationType;
        this.allowTemporalInjectorCallbackParameter = allowTemporalInjectorCallbackParameter;
        this.allowInjectParameter = allowInjectParameter;
        this.allowPluginParameter = allowPluginParameter;
    }

    protected abstract Class<? extends BeanProcessor<?>>[] getProcessors(A annotationInstance);

    @Override
    public void process(Phase phase, Object bean, A annotationInstance, AnnotatedElement annotatedElement, TemporalInjectorCallback callback) throws Exception {
        if (annotatedElement instanceof Class) {
            Class clazz = (Class<?>) annotatedElement;
            Class<? extends BeanProcessor<?>>[] processors = getProcessors(annotationInstance);

            if (processors.length == 0) {
                throw new ValidatorException("The " + ValidatorUtils.getDescription(clazz) + " is annotated with @"
                        + annotationType.getSimpleName() + " but specifies no " + BeanProcessor.class.getSimpleName()
                        + " types to inject and execute.");
            }
        } else if (annotatedElement instanceof Method) {
            Method method = (Method) annotatedElement;

            if (Modifier.isStatic(method.getModifiers())) {
                throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                        + annotationType.getSimpleName() + " but is declared static, which is not allowed.");
            } else {
                int parameterNumber = 0;
                for (Parameter parameter: method.getParameters()) {
                    parameterNumber++;
                    if (parameter.getType().isAssignableFrom(Phase.class)) {
                        continue;
                    } else if (parameter.getType().isAssignableFrom(TemporalInjectorCallback.class)) {
                        if (this.allowTemporalInjectorCallbackParameter) {
                            continue;
                        } else {
                            throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                                    + annotationType.getSimpleName() + " but its parameter #" + parameterNumber + " '" + parameter.getName()
                                    + "' requires an instance of the type " + TemporalInjectorCallback.class.getSimpleName()
                                    + " which is not available in this " + Phase.class.getSimpleName() + ".");
                        }
                    } else if (parameter.isAnnotationPresent(Inject.class)) {
                        if (this.allowInjectParameter) {
                            continue;
                        } else {
                            throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                                    + annotationType.getSimpleName() + " but its parameter #" + parameterNumber + " '" + parameter.getName()
                                    + "' is annotated with @" + Inject.class.getSimpleName() + " which is a functionality "
                                    + "that is not available in this " + Phase.class.getSimpleName() + ".");
                        }
                    } else if (parameter.isAnnotationPresent(Plugin.class)) {
                        if (this.allowPluginParameter) {
                            continue;
                        } else {
                            throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                                    + annotationType.getSimpleName() + " but its parameter #" + parameterNumber + " '" + parameter.getName()
                                    + "' is annotated with @" + Plugin.class.getSimpleName() + " which is a functionality "
                                    + "that is not available in this " + Phase.class.getSimpleName() + ".");
                        }
                    } else {
                        throw new ValidatorException("The " + ValidatorUtils.getDescription(method) + " is annotated with @"
                                + annotationType.getSimpleName() + " but its parameter #" + parameterNumber + " '" + parameter.getName()
                                + "' requires an instance of the type " + parameter.getType().getSimpleName()
                                + " which cannot be resolved.");
                    }
                }
            }
        }
    }
}