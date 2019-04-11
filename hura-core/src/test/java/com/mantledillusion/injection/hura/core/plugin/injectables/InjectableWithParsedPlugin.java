package com.mantledillusion.injection.hura.core.plugin.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.core.plugin.misc.ParsedInjectable;

public class InjectableWithParsedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "ParsingRequiredPlugin")
    public ParsedInjectable injectable;
}
