package com.mantledillusion.injection.hura.core.lifecycle.misc;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.AnnotationProcessor;
import com.mantledillusion.injection.hura.core.lifecycle.injectables.AbstractLifecycleInjectable;

public class LifecycleAnnotationProcessor implements AnnotationProcessor<ProcessedLifecycleInjectableAnnotation, Class<? extends AbstractLifecycleInjectable>> {

    @Override
    public void process(Phase phase, Object bean, ProcessedLifecycleInjectableAnnotation annotationInstance,
                        Class<? extends AbstractLifecycleInjectable> annotatedElement, Injector.TemporalInjectorCallback callback) throws Exception {
        AbstractLifecycleInjectable.add(phase, (AbstractLifecycleInjectable) bean, callback);
    }
}
