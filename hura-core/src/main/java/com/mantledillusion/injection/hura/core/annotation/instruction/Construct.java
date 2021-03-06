package com.mantledillusion.injection.hura.core.annotation.instruction;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Constructor}s that should be used by an
 * {@link Injector} when instantiating the {@link Constructor}'s {@link Class}.
 * <p>
 * If a {@link Constructor} is annotated with @{@link Construct}, all of its 0-n
 * parameters have to be annotated with @{@link Inject}, so these parameters can
 * be resolved by injecting them.
 * <p>
 * If no @{@link Construct} annotation is present on any {@link Constructor} of
 * a {@link Class}, the {@link Injector} will just search for the
 * {@link Constructor} with the least parameters that are all annotated
 * with @{@link Inject}. Note that the {@link Injector} will <b>not</b> make use
 * of unannotated inaccessible {@link Constructor}s.
 * <p>
 * The @{@link Construct} annotation can only be used once per {@link Class},
 * but of course multiple times in a {@link Class}es' hierarchy.
 * <p>
 * {@link Constructor}s annotated with @{@link Construct} may not:
 * <ul>
 * <li>have any {@link Parameter} that is not annotated with @{@link Inject}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
@PreConstruct(ConstructValidator.class)
public @interface Construct {

}
