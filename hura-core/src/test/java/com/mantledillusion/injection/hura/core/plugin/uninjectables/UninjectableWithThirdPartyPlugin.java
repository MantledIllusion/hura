package com.mantledillusion.injection.hura.core.plugin.uninjectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

public class UninjectableWithThirdPartyPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "ThirdPartyPluginRequiringPlugin")
    public InjectableInterface injectable;
}
