package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.Injector;

/**
 * {@link Annotation} for {@link String} {@link Field}s and {@link Parameter}s
 * who have to receive a property value when their {@link Class} is instantiated
 * and injected by an {@link Injector}.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Property} may
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
 * <li>@{@link DefaultValue}</li>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@Validated(PropertyValidator.class)
public @interface Property {

	/**
	 * The property key to resolve.
	 * 
	 * @return The property key to resolve; never null or empty
	 */
	String value();
}
