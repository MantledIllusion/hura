package com.mantledillusion.injection.hura.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.ValidatorUtils;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

abstract class AbstractLifecycleAnnotationValidator<A extends Annotation> implements AnnotationProcessor<A, AnnotatedElement> {

    private final Class<A> annotationType;

    AbstractLifecycleAnnotationValidator(Class<A> annotationType) {
        this.annotationType = annotationType;
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
                        continue;
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