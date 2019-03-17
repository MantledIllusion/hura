package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.annotation.lifecycle.annotation.PreConstruct;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ FIELD, PARAMETER })
@PreConstruct(PluginValidator.class)
public @interface Plugin {

    String directory();

    String pluginId();
}
