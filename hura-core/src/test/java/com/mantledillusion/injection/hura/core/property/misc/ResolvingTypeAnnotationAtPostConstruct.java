package com.mantledillusion.injection.hura.core.property.misc;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PostInject;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@PostInject(ResolverAtInspect.class)
public @interface ResolvingTypeAnnotationAtPostConstruct {

}
