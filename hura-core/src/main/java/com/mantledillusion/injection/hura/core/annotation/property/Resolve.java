package com.mantledillusion.injection.hura.core.annotation.property;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link String} {@link Field}s and {@link Parameter}s
 * who have to receive a property value when their {@link Class} is instantiated
 * and injected by an {@link Injector}.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Resolve} may
 * not:<br>
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * <li>be of any other type than {@link String}</li>
 * </ul>
 * <p>
 * Extensions to this {@link Annotation} are:
 * <ul>
 * <li>@{@link Matches}</li>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(ResolveValidator.class)
public @interface Resolve {

	/**
	 * The property key to resolve.
	 * 
	 * @return The property key to resolve; never null or empty
	 */
	String value();
}
