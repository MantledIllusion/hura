package com.mantledillusion.injection.hura.plugin.injectables;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;

public class InjectableWithInjectableInterfacePlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin")
    public InjectableInterface injectable;
}
