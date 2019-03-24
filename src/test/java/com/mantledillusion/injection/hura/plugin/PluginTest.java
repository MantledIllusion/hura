package com.mantledillusion.injection.hura.plugin;

import com.mantledillusion.injection.hura.AbstractInjectionTest;
import com.mantledillusion.injection.hura.Predefinable;
import com.mantledillusion.injection.hura.exception.PluginException;
import com.mantledillusion.injection.hura.plugin.injectables.*;
import com.mantledillusion.injection.hura.plugin.misc.ParsedInjectable;
import com.mantledillusion.injection.hura.plugin.uninjectables.UninjectableWithJarExtensionPlugin;
import com.mantledillusion.injection.hura.plugin.uninjectables.UninjectableWithThirdPartyPlugin;
import com.mantledillusion.injection.hura.plugin.uninjectables.UninjectableWithVersionedPlugin;
import org.junit.Assert;
import org.junit.Test;

public class PluginTest extends AbstractInjectionTest {

    private static final String DEFAULT_PROPERTY = "someProperty";

    @Test
    public void testInjectPlugin() {
        InjectableWithInjectableInterfacePlugin injectable = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);

        Assert.assertNotNull(injectable.injectable);
    }

    @Test
    public void testPluginReuseCaching() {
        InjectableWithInjectableInterfacePlugin injectableA = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);
        InjectableWithInjectableInterfacePlugin injectableB = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);

        Assert.assertSame(injectableA.injectable.getClass(), injectableB.injectable.getClass());
    }

    @Test
    public void testPluginCopyCaching() {
        InjectableWithInjectableInterfacePlugin injectableA = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);
        InjectableWithInjectableInterfacePluginCopy injectableB = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePluginCopy.class);

        Assert.assertSame(injectableA.injectable.getClass(), injectableB.injectable.getClass());
    }

    @Test
    public void testPluginVersionPriorizing() {
        InjectableWithVersionPromotingPlugin injectable = this.suite.injectInSuiteContext(InjectableWithVersionPromotingPlugin.class);

        Assert.assertEquals(2, injectable.injectable.getVersion());
    }

    @Test(expected = PluginException.class)
    public void testPluginWithFullFileName() {
        this.suite.injectInSuiteContext(UninjectableWithJarExtensionPlugin.class);
    }

    @Test(expected = PluginException.class)
    public void testPluginWithVersionedPluginId() {
        this.suite.injectInSuiteContext(UninjectableWithVersionedPlugin.class);
    }

    @Test(expected = PluginException.class)
    public void testPluginWithThirdPartyPluginRequirement() {
        this.suite.injectInSuiteContext(UninjectableWithThirdPartyPlugin.class);
    }

    @Test
    public void testPluginWithFirstPartyPluginRequirement() {
        InjectableWithFirstPartyPlugin injectable = this.suite.injectInSuiteContext(InjectableWithFirstPartyPlugin.class);

        Assert.assertNotNull(injectable.injectable.getBean());
    }

    @Test
    public void testPluginParsing() {
        InjectableWithParsedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithParsedPlugin.class,
                Predefinable.Property.of(ParsedInjectable.PROPERTY_KEY, DEFAULT_PROPERTY));

        Assert.assertEquals(DEFAULT_PROPERTY, injectable.injectable.getProperty());
    }

    @Test
    public void testPluginRootBeanInjection() {
        InjectableWithRootBeanInjectedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithRootBeanInjectedPlugin.class);

        Assert.assertNotNull(injectable.injectable.getBean());
    }

    @Test
    public void testPluginPluginBeanInjection() {
        InjectableWithPluginBeanInjectedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithPluginBeanInjectedPlugin.class);

        Assert.assertNotNull(injectable.injectable.getBean());
    }
}
