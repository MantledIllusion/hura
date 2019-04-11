package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

public class InjectableWithInjectableInterfacePlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin")
    public InjectableInterface injectable;
}
