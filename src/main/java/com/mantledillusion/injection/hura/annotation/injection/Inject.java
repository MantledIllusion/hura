package com.mantledillusion.injection.hura.annotation.injection;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Predefinable.Singleton;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s who have to be
 * injected by an {@link Injector} when their {@link Class} is instantiated and
 * injected by one.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Inject} may not:
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * </ul>
 * <p>
 * Extensions to this {@link Annotation} are:
 * <ul>
 * <li>@{@link Qualifier}</li>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(InjectValidator.class)
public @interface Inject {

	/**
	 * Flag that indicates whether to overwrite the value of an annotated
	 * {@link Field} with null if the resolved bean to inject is null.
	 * <p>
	 * A null bean might be resolved if @{@link Optional} is present and no bean is
	 * available, or if there is a specific {@link BeanAllocation}/{@link Singleton}
	 * that allocates to null.
	 * <p>
	 * Of course annotated {@link Parameter}s cannot be preset with any value, so
	 * their constructor/method will always be called with null in that
	 * {@link Parameter}s place.
	 * <p>
	 * By default the flag is false, nulls do not overwrite an injected
	 * {@link Field}'s value.
	 * 
	 * @return True if a {@link Field}s value has to be overwritten with null when
	 *         the allocated bean is null; false otherwise
	 */
	boolean overwriteWithNull() default false;
}
