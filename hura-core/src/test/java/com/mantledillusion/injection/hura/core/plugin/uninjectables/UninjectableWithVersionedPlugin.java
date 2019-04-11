package com.mantledillusion.injection.hura.core.plugin.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.VersionPromotingInjectable;

public class UninjectableWithVersionedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "VersionPromotingPlugin_v1")
    public VersionPromotingInjectable injectable;
}
