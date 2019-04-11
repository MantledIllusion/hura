package com.mantledillusion.injection.hura.core.annotation.instruction;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for beans that represent a context sensitive entity.
 * <p>
 * For example, in an injection where:
 * <ul>
 * <li>{@link Class} A is the root type</li>
 * <li>A has a reference to the injected {@link Context} of {@link Class} C</li>
 * <li>C carries an {@link Integer} that determines what index its parent A
 * instance is in a range from 0-n</li>
 * </ul>
 * ... the {@link Class} C should be annotated with @{@link Context} as
 * instances of C have a definite relation to the injection context they are in,
 * making them context sensitive.
 * <p>
 * {@link Class}es that are context sensitive (because any super {@link Class}
 * or interface in their hierarchy is annotated with @{@link Context}) are
 * treated specially regarding their injection:
 * <ul>
 * <li>They cannot be instantiated by the {@link Injector}, they have to be
 * pre-defined by the {@link Blueprint}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Context {

}
