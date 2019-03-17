package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Predefinable;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Extension {@link Annotation} to @{@link Inject}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Inject}
 * and @{@link Qualifier} will be injected with a named {@link Predefinable.Singleton}
 * instead of an anonymous independent bean.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Qualifier} may not:
 * <ul>
 * <li>be not annotated with @{@link Inject}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
@PreConstruct(QualifierValidator.class)
public @interface Qualifier {

    /**
     * The qualifier.
     *
     * @return The qualifier under which the {@link Predefinable.Singleton} to inject into the
     * annotated {@link Field}/{@link Parameter} is registered in its injection context;
     * never null, might be blank if no {@link Predefinable.Singleton} but an independent bean is desired
     */
    String value();

    /**
     * The singleton mode.
     * <p>
     * {@link SingletonMode#SEQUENCE} by default.
     *
     * @return The singleton mode, which specifies which singleton pool to take the singleton
     * with the given qualifier from
     */
    SingletonMode mode() default SingletonMode.SEQUENCE;
}