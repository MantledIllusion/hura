package com.mantledillusion.injection.hura.property.misc;

import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PostInject;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@PostInject(ResolverAtInspect.class)
public @interface ResolvingTypeAnnotationAtPostConstruct {

}
