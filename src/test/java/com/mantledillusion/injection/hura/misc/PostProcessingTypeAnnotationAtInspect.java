package com.mantledillusion.injection.hura.misc;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inspected;

@Retention(RUNTIME)
@Target(TYPE)
@Inspected(value=TypeInspectorAtInspect.class, phase=Phase.INSPECT)
public @interface PostProcessingTypeAnnotationAtInspect {

}
