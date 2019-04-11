package com.mantledillusion.injection.hura.core.lifecycle.misc;


import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE})
@PreConstruct(LifecycleAnnotationProcessor.class)
@PostInject(LifecycleAnnotationProcessor.class)
@PostConstruct(LifecycleAnnotationProcessor.class)
@PreDestroy(LifecycleAnnotationProcessor.class)
@PostDestroy(LifecycleAnnotationProcessor.class)
public @interface ProcessedLifecycleInjectableAnnotation {

}
