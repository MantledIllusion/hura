package com.mantledillusion.injection.hura.core.plugin.uninjectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Plugin;

public class UninjectableWithDuplicateTypeBindingPlugin {

    @Plugin(directory = "src/test/resources/plugins", pluginId = "DuplicateInjectableInterfacePlugin")
    public InjectableInterface injectable;
}
