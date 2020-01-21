package com.mantledillusion.injection.hura.core.annotation.lifecycle.bean;

import com.mantledillusion.injection.hura.core.service.InjectionProvider;
import com.mantledillusion.injection.hura.core.service.ResolvingProvider;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Class}es and {@link Method}s that need to be called at the {@link Phase#POST_INJECT}
 * phase of a bean's life cycle.
 * <p>
 * {@link Method}s annotated with @{@link PostInject} support the following parameters:<br>
 * <ul>
 * <li>Of the type {@link Phase}</li>
 * <li>Of the type {@link InjectionProvider}</li>
 * <li>Of the type {@link ResolvingProvider}</li>
 * <li>Any type annotated with {@link Inject}</li>
 * <li>Any type annotated with {@link Plugin}</li>
 * <li>Any type annotated with {@link Resolve}</li>
 * </ul>
 * <p>
 * {@link Method}s annotated with @{@link PostInject} may not:<br>
 * <ul>
 * <li>be static</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@PreConstruct(PostInjectValidator.class)
public @interface PostInject {

	/**
	 * The {@link BeanProcessor} implementations to inject and apply on bean instances of a {@link Class} or
	 * {@link Method}'s {@link Class} annotated with @{@link PostInject}.
	 *
	 * @return The {@link BeanProcessor} implementation to inject and execute on a bean; never null
	 */
	Class<? extends BeanProcessor<?>>[] value() default {};
}