package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.VersionPromotingInjectable;

public class UninjectableWithThirdPartyPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "ThirdPartyPluginRequiringPlugin")
    public InjectableInterface injectable;
}
