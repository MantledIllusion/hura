package com.mantledillusion.injection.hura.plugin.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Plugin;
import com.mantledillusion.injection.hura.plugin.misc.ParsedInjectable;

public class InjectableWithParsedPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "ParsingRequiredPlugin")
    public ParsedInjectable injectable;
}
