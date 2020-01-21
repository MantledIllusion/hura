package com.mantledillusion.injection.hura.core.property.misc;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProcessorResolver;

public class ResolverAtInspect implements AnnotationProcessor<ResolvingTypeAnnotationAtPostConstruct, Class<?>> {

    @Override
    public void process(Phase phase, Object bean, ResolvingTypeAnnotationAtPostConstruct annotationInstance,
                        Class<?> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        ((InjectableWithProcessorResolver) bean).propertyValue = callback.resolve("${property.key}");
    }
}
