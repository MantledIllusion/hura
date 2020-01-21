package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Class}es and {@link Method}s that need to be called at the {@link Phase#POST_DESTROY}
 * phase of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link PostDestroy} support the following parameters:<br>
 * <ul>
 * <li>Of the type {@link Phase}</li>
 * </ul>
 * <p>
 * {@link Method}s annotated with @{@link PostDestroy} may not:<br>
 * <ul>
 * <li>be static</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@PreConstruct(PostDestroyValidator.class)
public @interface PostDestroy {

	/**
	 * The {@link BeanProcessor} implementations to inject and apply on bean instances of a {@link Class} or
	 * {@link Method}'s {@link Class} annotated with @{@link PostDestroy}.
	 *
	 * @return The {@link BeanProcessor} implementation to inject and execute on a bean; never null
	 */
	Class<? extends BeanProcessor<?>>[] value() default {};
}