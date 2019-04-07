package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

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
 * {@link Annotation} for {@link Field}s and {@link Parameter}s who have to be
 * injected by an {@link Injector} by using the service provider from the given
 * plugin for the annotated SPI.
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
     * The directory the plugin can be found in.
     *
     * @return The directories path; has to exist and evaluate
     * {@link File#isDirectory()} with true
     */
    String directory();

    /**
     * The ID of the plugin to load, which determines the .JARs in the given
     * directory that are considered a matching plugin for the injection.
     * <p>
     * NOTE: This is <b>not</b> the file name; the plugin id is neither
     * versioned or ends with a '.jar' extension. For example, in a directory
     * with 2 .JARs 'samplePlugin.jar' and 'samplePlugin_v2.jar', the plugin
     * id would simply be 'samplePlugin'.
     * <p>
     * When encountering multiple plugins with matching plugin ids in a
     * directory, the one with the highest version suffix is used.
     *
     * @return The plugin id;
     */
    String pluginId();
}
