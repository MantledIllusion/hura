package com.mantledillusion.injection.hura.annotation.property;

import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * Extension {@link Annotation} to @{@link Property}.
 * <p>
 * A {@link Field}/{@link Parameter} annotated with @{@link Property}
 * and @{@link DefaultValue} will be set to the default value if the property is
 * not resolvable otherwise.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link DefaultValue} may not:
 * <ul>
 * <li>be not annotated with @{@link Property}</li>
 * <li>be annotated with @{@link Matches} and not match its matcher</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(DefaultValueValidator.class)
public @interface DefaultValue {

	/**
	 * The default value to be set if the property is not resolvable.
	 * 
	 * @return The default value to use; never null
	 */
	String value();
}
