package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;

public class UninjectableWithUnknownSpiClassPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "UnknownSpiClassPlugin")
    public InjectableInterface injectable;
}
