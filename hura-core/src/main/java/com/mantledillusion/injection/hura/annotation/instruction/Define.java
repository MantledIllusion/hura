package com.mantledillusion.injection.hura.annotation.instruction;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Blueprint.SingletonAllocation;
import com.mantledillusion.injection.hura.Blueprint.PropertyAllocation;
import com.mantledillusion.injection.hura.Blueprint.MappingAllocation;
import com.mantledillusion.injection.hura.Blueprint.TypeAllocation;

/**
 * {@link Annotation} for {@link Method}s of {@link Blueprint}
 * implementations that define allocations to influence the way a bean
 * is injected by an {@link Injector}.
 * <p>
 * {@link Method}s annotated with @{@link Define} may not:
 * <ul>
 * <li>have a return type any other than:
 * <ul>
 * <li>{@link SingletonAllocation}</li>
 * <li>{@link PropertyAllocation}</li>
 * <li>{@link MappingAllocation}</li>
 * <li>{@link TypeAllocation}</li>
 * <li>{@link Collection} of such {@link Blueprint.Allocation}s</li>
 * </ul>
 * </li>
 * <li>have {@link Parameter}s</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Define {

}