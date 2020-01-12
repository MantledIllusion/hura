package com.mantledillusion.injection.hura.core.annotation.event;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link java.lang.reflect.Method}s who receive events from the
 * event {@link com.mantledillusion.injection.hura.core.Bus}.
 * <p>
 * The {@link java.lang.reflect.Method} will be auto-registered for specific event classes; which ones depend on the
 * {@link java.lang.reflect.Method}s parameter and the declared {@link #value()}.
 * <p>
 * The {@link java.lang.reflect.Method} will also receive any events of sub-{@link Class}es of the event {@link Class}es
 * it is registered for.
 * <p>
 * {@link java.lang.reflect.Method}s annotated with @{@link Subscribe} may not:
 * <ul>
 * <li>be a static {@link java.lang.reflect.Method}</li>
 * <li>declare more than one {@link java.lang.reflect.Parameter}</li>
 * <li>declare no {@link java.lang.reflect.Parameter} without specifying at least one extension {@link Class}</li>
 * <li>declare an extension {@link Class} that is not assignable by its {@link java.lang.reflect.Parameter}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(METHOD)
@PreConstruct(SubscribeValidator.class)
public @interface Subscribe {

    /**
     * Event {@link Class}es the annotated {@link java.lang.reflect.Method} is interested in.
     * <p>
     * If the {@link java.lang.reflect.Method} has a {@link java.lang.reflect.Parameter}...
     * <ul>
     * <li>...it always needs to be assignable from every extension {@link Class}es specified</li>
     * <li>...it will not be used for registration; instead, the extension {@link Class}es are</li>
     * </ul>
     *
     * @return The event {@link Class}es to register for, never null
     */
    Class<?>[] value() default {};
}
