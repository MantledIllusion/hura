package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Predefinable;
import com.mantledillusion.injection.hura.Predefinable.Singleton;

/**
 * {@link Annotation} for {@link Method}s of {@link BlueprintTemplate}
 * implementations that define allocations to influence the way the processed
 * {@link TypedBlueprint} is injected by an {@link Injector}.
 * <p>
 * {@link Method}s annotated with @{@link Define} may not:
 * <ul>
 * <li>have a return type any other than:
 * <ul>
 * <li>{@link Singleton}</li>
 * <li>{@link Property}</li>
 * <li>{@link Collection} of such {@link Predefinable}s</li>
 * <li>{@link BeanAllocation}</li>
 * </ul>
 * </li>
 * <li>have {@link Parameter}s</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(METHOD)
@Validated(DefineValidator.class)
public @interface Define {

}
