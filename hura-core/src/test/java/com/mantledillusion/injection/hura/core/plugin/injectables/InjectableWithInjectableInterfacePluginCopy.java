package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

public class InjectableWithInjectableInterfacePluginCopy {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePluginCopy")
    public InjectableInterface injectable;
}
