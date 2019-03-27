package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;

public class UninjectableWithUnknownPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "UnknownPlugin")
    public InjectableInterface injectable;
}
