package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.VersionPromotingInjectable;

public class InjectableWithVersionPromotingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "VersionPromotingPlugin")
    public VersionPromotingInjectable injectable;
}
