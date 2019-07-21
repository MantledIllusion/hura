package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.VersionPromotingInjectable;

public class InjectableWithResolvedPlugin {

    public static final String PKEY_DIR = "directory";
    public static final String PKEY_PLUGINID = "pluginId";
    public static final String PKEY_VERSIONFROM = "versionFrom";
    public static final String PKEY_VERSIONUNTIL = "versionUntil";

    @Plugin(directory = "${"+PKEY_DIR+"}", pluginId = "${"+PKEY_PLUGINID+"}",
            versionFrom = "${"+PKEY_VERSIONFROM+"}", versionUntil = "${"+PKEY_VERSIONUNTIL+"}")
    public VersionPromotingInjectable injectable;
}
