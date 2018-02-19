package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

/**
 * {@link Annotation} for beans that represent a context sensitive entity.
 * <p>
 * For example, in an injection where:<br>
 * - {@link Class} A is the root type<br>
 * - A has a reference to the injected {@link Context} of {@link Class} C<br>
 * - C carries an {@link Integer} that determines what index its parent A
 * instance is in a range from 0-n<br>
 * ... the {@link Class} C should be annotated with @{@link Context} as
 * instances of C have a definite relation to the injection context they are in,
 * making them context sensitive.
 * <p>
 * {@link Class}es that are context sensitive (because any super {@link Class}
 * or interface in their hierarchy is annotated with @{@link Context}) are
 * treated specially regarding their injection:<br>
 * - They cannot be instantiated by the {@link Injector}, they have to be
 * pre-defined by the {@link TypedBlueprint}<br>
 * - They cannot be defined as {@link SingletonMode#GLOBAL} singletons, since
 * that would allow carrying them out of their injection context, harming their
 * sensitivity.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Context {

}
