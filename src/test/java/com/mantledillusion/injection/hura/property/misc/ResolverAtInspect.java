package com.mantledillusion.injection.hura.property.misc;


import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProcessorResolver;

public class ResolverAtInspect implements AnnotationProcessor<ResolvingTypeAnnotationAtPostConstruct, Class<?>> {

    @Override
    public void process(Phase phase, Object bean, ResolvingTypeAnnotationAtPostConstruct annotationInstance,
                        Class<?> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        ((InjectableWithProcessorResolver) bean).propertyValue = callback.resolve("property.key");
    }
}
