package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.VersionPromotingInjectable;

public class InjectableWithVersionExcludingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "VersionPromotingPlugin",
            versionFrom = "1.0", versionUntil = "2.0")
    public VersionPromotingInjectable injectable;
}
