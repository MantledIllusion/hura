package com.mantledillusion.injection.hura.core.plugin;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.exception.PluginException;
import com.mantledillusion.injection.hura.core.plugin.injectables.*;
import com.mantledillusion.injection.hura.core.plugin.misc.ParsedInjectable;
import com.mantledillusion.injection.hura.core.plugin.uninjectables.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PluginTest extends AbstractInjectionTest {

    private static final String DEFAULT_PROPERTY = "someProperty";

    @Test
    public void testInjectPlugin() {
        InjectableWithInjectableInterfacePlugin injectable = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);

        Assertions.assertNotNull(injectable.injectable);
    }

    @Test
    public void testInjectUnknownPlugin() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInSuiteContext(UninjectableWithUnknownPlugin.class));
    }

    @Test
    public void testPluginReuseCaching() {
        InjectableWithInjectableInterfacePlugin injectableA = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);
        InjectableWithInjectableInterfacePlugin injectableB = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);

        Assertions.assertSame(injectableA.injectable.getClass(), injectableB.injectable.getClass());
    }

    @Test
    public void testPluginCopyCaching() {
        InjectableWithInjectableInterfacePlugin injectableA = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePlugin.class);
        InjectableWithInjectableInterfacePluginCopy injectableB = this.suite.injectInSuiteContext(InjectableWithInjectableInterfacePluginCopy.class);

        Assertions.assertSame(injectableA.injectable.getClass(), injectableB.injectable.getClass());
    }

    @Test
    public void testPluginVersionPriorizing() {
        InjectableWithVersionPromotingPlugin injectable = this.suite.injectInSuiteContext(InjectableWithVersionPromotingPlugin.class);

        Assertions.assertEquals(2, injectable.injectable.getVersion());
    }

    @Test
    public void testPluginVersionExclusion() {
        InjectableWithVersionExcludingPlugin injectable = this.suite.injectInSuiteContext(InjectableWithVersionExcludingPlugin.class);

        Assertions.assertEquals(1, injectable.injectable.getVersion());
    }

    @Test
    public void testResolvedPlugin() {
        InjectableWithResolvedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithResolvedPlugin.class,
                Blueprint.PropertyAllocation.of(InjectableWithResolvedPlugin.PKEY_DIR, "src/test/resources/plugins"),
                Blueprint.PropertyAllocation.of(InjectableWithResolvedPlugin.PKEY_PLUGINID, "VersionPromotingPlugin"),
                Blueprint.PropertyAllocation.of(InjectableWithResolvedPlugin.PKEY_VERSIONFROM, "1.0"),
                Blueprint.PropertyAllocation.of(InjectableWithResolvedPlugin.PKEY_VERSIONUNTIL, "2.0"));

        Assertions.assertEquals(1, injectable.injectable.getVersion());
    }

    @Test
    public void testPluginWithFullFileName() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInSuiteContext(UninjectableWithJarExtensionPlugin.class));
    }

    @Test
    public void testPluginWithVersionedPluginId() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInSuiteContext(UninjectableWithVersionedPlugin.class));
    }

    @Test
    public void testPluginWithThirdPartyPluginRequirement() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInSuiteContext(UninjectableWithThirdPartyPlugin.class));
    }

    @Test
    public void testPluginWithFirstPartyPluginRequirement() {
        InjectableWithFirstPartyPlugin injectable = this.suite.injectInSuiteContext(InjectableWithFirstPartyPlugin.class);

        Assertions.assertNotNull(injectable.injectable.getBean());
    }

    @Test
    public void testPluginParsing() {
        InjectableWithParsedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithParsedPlugin.class,
                Blueprint.PropertyAllocation.of(ParsedInjectable.PROPERTY_KEY, DEFAULT_PROPERTY));

        Assertions.assertEquals(DEFAULT_PROPERTY, injectable.injectable.getProperty());
    }

    @Test
    public void testPluginRootBeanInjection() {
        InjectableWithRootBeanInjectedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithRootBeanInjectedPlugin.class);

        Assertions.assertNotNull(injectable.injectable.getBean());
    }

    @Test
    public void testPluginPluginBeanInjection() {
        InjectableWithPluginBeanInjectedPlugin injectable = this.suite.injectInSuiteContext(InjectableWithPluginBeanInjectedPlugin.class);

        Assertions.assertNotNull(injectable.injectable.getBean());
    }

    @Test
    public void testMissingTypeBinding() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithMissingTypeBindingPlugin.class));
    }

    @Test
    public void testDuplicateTypeBinding() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithDuplicateTypeBindingPlugin.class));
    }

    @Test
    public void testMetaInvalidSpiClass() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithInvalidSpiClassPlugin.class));
    }

    @Test
    public void testMetaUnknownSpiClass() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithUnknownSpiClassPlugin.class));
    }

    @Test
    public void testMetaInvalidServiceProviderClass() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithInvalidServiceProviderClassPlugin.class));
    }

    @Test
    public void testMetaUnknownServiceProviderClass() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithUnknownServiceProviderClassPlugin.class));
    }

    @Test
    public void testMetaUnassignableServiceProvider() {
        Assertions.assertThrows(PluginException.class, () -> this.suite.injectInRootContext(UninjectableWithUnassignableServiceProviderPlugin.class));
    }
}
