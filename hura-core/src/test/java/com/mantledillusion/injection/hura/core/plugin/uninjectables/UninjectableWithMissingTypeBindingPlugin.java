package com.mantledillusion.injection.hura.core.plugin.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

import java.io.Serializable;

public class UninjectableWithMissingTypeBindingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin")
    public Serializable injectable;
}
