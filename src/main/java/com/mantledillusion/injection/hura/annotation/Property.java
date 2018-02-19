package com.mantledillusion.injection.hura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.exception.ResolvingException;

/**
 * {@link Annotation} for {@link String} {@link Field}s and {@link Parameter}s
 * who have to receive a property value when their {@link Class} is instantiated
 * and injected by an {@link Injector}.
 * <p>
 * {@link Field}s and {@link Parameter}s annotated with @{@link Property} may not:<br>
 * - be of any other type than {@link String}<br>
 * {@link Field}s annotated with @{@link Property} additionally may not:<br>
 * - be static<br>
 * - be final
 */
@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@Validated(PropertyValidator.class)
public @interface Property {

	public static final String DEFAULT_MATCHER = ".*";

	/**
	 * The property key to resolve.
	 * 
	 * @return The property key to resolve; never null
	 */
	String value();

	/**
	 * The {@link Pattern} matcher for the {@link Property} value to match.
	 * 
	 * @return The matcher for the {@link Property}s value; never null, must be
	 *         parsable by {@link Pattern#compile(String)}, '.*' by default to match
	 *         every possible value
	 */
	String matcher() default DEFAULT_MATCHER;

	/**
	 * Determines whether the {@link Property} has to be resolvable.
	 * <p>
	 * If set to true, the {@link Annotation} will cause a
	 * {@link ResolvingException} to be thrown if the property cannot be resolved.
	 * <p>
	 * If set to false and the property cannot be resolved, the {@link Annotation}
	 * will either cause the {@link #defaultValue()} to be set if
	 * {@link #useDefault()}==true, or the property key will be set.
	 * 
	 * @return True if the {@link Property} always has to be resolvable, false
	 *         otherwise; false by default
	 */
	boolean forced() default false;

	/**
	 * Determines whether to use the {@link #defaultValue()} if the property cannot
	 * be resolved.
	 * <p>
	 * Note that this setting only has effect if {@link #forced()}==false.
	 * 
	 * @return True if the {@link Property} should be set to the
	 *         {@link #defaultValue()} if it is not resolvable, false if it should
	 *         be set to the property key in that case; false by default
	 */
	boolean useDefault() default false;

	/**
	 * The default value to be set if the property is not resolvable.
	 * <p>
	 * Note that the default value is only applied if {@link #forced()}==false and
	 * {@link #useDefault()}==true. In that case the default value also has to match
	 * the {@link #matcher()}.
	 * 
	 * @return The default value to use; never null, empty by default
	 */
	String defaultValue() default "";
}
