package com.mantledillusion.injection.hura.core.annotation.injection;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.annotation.PreConstruct;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link Annotation} for {@link Field}s and {@link Parameter}s who have to be injected by an {@link Injector} by using
 * the service provider from the given plugin for the annotated SPI.
 * <p>
 * Plugins are versioned by using a '_v...' file name infix right before the '.jar' extension for example
 * 'samplePlugin_v3.0.1.jar'. This is not mandatory, so 'samplePlugin.jar' will be accepted as a version 0 plugin.
 * <p>
 * When encountering multiple plugins with matching plugin ids in a directory that also match the version range
 * defined by {@link #versionFrom()} and {@link #versionUntil()}, the one with the highest version is used.
 * <p>
 * {@link Field}s/{@link Parameter}s annotated with @{@link Plugin} may not:
 * <ul>
 * <li>be a static {@link Field}</li>
 * <li>be a final {@link Field}</li>
 * <li>be also annotated with @{@link Inject}</li>
 * <li>be also annotated with @{@link Aggregate}</li>
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
@PreConstruct(PluginValidator.class)
public @interface Plugin {

    /**
     * A {@link String} {@link java.util.regex.Pattern} for valid {@link Integer}s.
     */
    String NUMBER_PATTERN = "(0|([1-9][0-9]*))";

    /**
     * A {@link String} {@link java.util.regex.Pattern} for '.' separated plugin versions like:
     * <ul>
     * <li>1.0</li>
     * <li>1</li>
     * <li>0</li>
     * <li>4.2.0.9</li>
     * </ul>
     */
    String VERSION_PATTERN = NUMBER_PATTERN+"(\\."+NUMBER_PATTERN+")*";

    /**
     * The directory the plugin can be found in.
     * <p>
     * <b>Resolvable Value</b>; properties can be used within it.
     *
     * @return The directories path, has to exist and evaluate {@link File#isDirectory()} with true
     */
    String directory();

    /**
     * The ID of the plugin to load, which determines the .JARs in the given directory that are considered a matching
     * plugin for the injection.
     * <p>
     * NOTE: This is <b>not</b> the file name; the plugin id is neither versioned nor ends with a '.jar' extension.
     * For example, in a directory with 2 .JARs 'samplePlugin.jar' and 'samplePlugin_v2.0.jar', the plugin id would
     * simply be 'samplePlugin'.
     * <p>
     * <b>Resolvable Value</b>; properties can be used within it.
     *
     * @return The plugin id, never null
     */
    String pluginId();

    /**
     * The minimum (inclusive) version the loaded plugin is allowed to have.
     * <p>
     * Has to match the pattern {@link #VERSION_PATTERN}, '0' by default.
     * <p>
     * <b>Resolvable Value</b>; properties can be used within it.
     *
     * @return The version pattern, never null
     */
    String versionFrom() default "0";

    /**
     * The maximum (exclusive) version the loaded plugin is not allowed to have anymore.
     * <p>
     * Has to match the pattern {@link #VERSION_PATTERN}, {@link Integer#MAX_VALUE} by default.
     * <p>
     * <b>Resolvable Value</b>; properties can be used within it.
     *
     * @return The version pattern, never null
     */
    String versionUntil() default ""+Integer.MAX_VALUE;
}
