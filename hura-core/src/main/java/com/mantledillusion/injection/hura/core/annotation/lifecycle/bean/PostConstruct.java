package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Class}es and {@link Method}s that need to be called at the {@link Phase#POST_CONSTRUCT}
 * phase of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link PostConstruct} support the following parameters:<br>
 * <ul>
 * <li>Of the type {@link Phase}</li>
 * <li>Any type annotated with {@link Resolve}</li>
 * </ul>
 * <p>
 * {@link Method}s annotated with @{@link PostConstruct} may not:<br>
 * <ul>
 * <li>be static</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@PreConstruct(PostConstructValidator.class)
public @interface PostConstruct {

    /**
     * The {@link BeanProcessor} implementations to inject and apply on bean instances of a {@link Class} or
     * {@link Method}'s {@link Class} annotated with @{@link PostConstruct}.
     *
     * @return The {@link BeanProcessor} implementation to inject and execute on a bean; never null
     */
    Class<? extends BeanProcessor<?>>[] value() default {};
}