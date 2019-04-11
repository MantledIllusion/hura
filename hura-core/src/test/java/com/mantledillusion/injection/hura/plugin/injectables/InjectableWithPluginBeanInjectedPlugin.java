package com.mantledillusion.injection.hura.plugin.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.InjectedInjectable;

public class InjectableWithPluginBeanInjectedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "PluginBeanRequiredPlugin")
    public InjectedInjectable injectable;
}
