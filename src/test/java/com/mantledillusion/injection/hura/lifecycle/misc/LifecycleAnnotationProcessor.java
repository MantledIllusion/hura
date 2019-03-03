package com.mantledillusion.injection.hura.lifecycle.misc;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.lifecycle.injectables.AbstractLifecycleInjectable;

public class LifecycleAnnotationProcessor implements AnnotationProcessor<ProcessedLifecycleInjectableAnnotation, Class<? extends AbstractLifecycleInjectable>> {

    @Override
    public void process(Phase phase, Object bean, ProcessedLifecycleInjectableAnnotation annotationInstance,
                        Class<? extends AbstractLifecycleInjectable> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, (AbstractLifecycleInjectable) bean, callback);
    }
}
