package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.core.plugin.misc.InjectedInjectable;

public class InjectableWithPluginParameterMethodDuringPostConstructPhase {

    @PostConstruct
    public void process(@Plugin(directory = "src/test/resources/plugins", pluginId = "PluginBeanRequiredPlugin") InjectedInjectable injectable) {
    }
}
