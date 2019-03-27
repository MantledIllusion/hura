package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;

import java.io.Serializable;

public class UninjectableWithMissingTypeBindingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin")
    public Serializable injectable;
}
