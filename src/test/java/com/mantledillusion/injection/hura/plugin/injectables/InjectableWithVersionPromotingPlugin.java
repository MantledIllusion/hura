package com.mantledillusion.injection.hura.plugin.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.VersionPromotingInjectable;

public class InjectableWithVersionPromotingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "VersionPromotingPlugin")
    public VersionPromotingInjectable injectable;
}
