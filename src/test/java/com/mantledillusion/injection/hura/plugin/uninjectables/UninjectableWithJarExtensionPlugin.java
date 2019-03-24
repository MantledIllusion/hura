package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;

public class UninjectableWithJarExtensionPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin.jar")
    public InjectableInterface injectable;
}
