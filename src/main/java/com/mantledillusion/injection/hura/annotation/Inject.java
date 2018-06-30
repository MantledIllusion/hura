package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import org.apache.commons.lang3.StringUtils;

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
 * <li>@{@link Global}</li>
 * <li>@{@link Optional}</li>
 * </ul>
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@Validated(InjectValidator.class)
public @interface Inject {

	/**
	 * The qualifier.
	 * <p>
	 * By default the used qualifier is "", meaning independent (no
	 * {@link Singleton}).
	 * 
	 * @return The qualifier under which the {@link Singleton} to inject into the
	 *         annotated {@link Field}/{@link Parameter} is registered in its
	 *         injection context; never null, might be blank if no {@link Singleton}
	 *         but an independent bean is desired
	 */
	String value() default StringUtils.EMPTY;

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
