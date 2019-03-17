package com.mantledillusion.injection.hura.plugin;

import com.mantledillusion.injection.hura.AbstractInjectionTest;
import com.mantledillusion.injection.hura.plugin.injectables.InjectableWithSerializablePlugin;
import org.junit.Assert;
import org.junit.Test;

public class PluginTest extends AbstractInjectionTest {

    @Test
    public void testInjectPlugin() {
        InjectableWithSerializablePlugin injectable = this.suite.injectInSuiteContext(InjectableWithSerializablePlugin.class);

        Assert.assertNotNull(injectable.injectable);
    }
}