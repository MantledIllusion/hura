package com.mantledillusion.injection.hura.plugin.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.VersionPromotingInjectable;

public class UninjectableWithVersionedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "VersionPromotingPlugin_v1")
    public VersionPromotingInjectable injectable;
}
