package com.mantledillusion.injection.hura.test;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.InjectedInjectable;

public class FirstPartyPluginRequiredInjectable implements InjectedInjectable {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "InjectableInterfacePlugin")
    public InjectableInterface injectable;

    @Override
    public InjectableInterface getBean() {
        return this.injectable;
    }
}
