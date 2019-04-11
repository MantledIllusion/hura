package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.InjectedInjectable;

public class InjectableWithRootBeanInjectedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "RootBeanRequiredPlugin")
    public InjectedInjectable injectable;
}
