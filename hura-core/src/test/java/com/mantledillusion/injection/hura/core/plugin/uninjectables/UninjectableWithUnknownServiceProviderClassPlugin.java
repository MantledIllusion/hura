package com.mantledillusion.injection.hura.core.plugin.uninjectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

public class UninjectableWithUnknownServiceProviderClassPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "UnknownServiceProviderClassPlugin")
    public InjectableInterface injectable;
}
