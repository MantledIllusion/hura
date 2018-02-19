package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.injectables.Injectable;
import com.mantledillusion.injection.hura.injectables.InjectableWithExtension;
import com.mantledillusion.injection.hura.misc.InjectableInterfaceExtension;

public class InjectionExtensionTest extends AbstractInjectionTest {

	@Test
	public void testExtension() {
		Injectable relayed = new Injectable();

		InjectableWithExtension injectable = this.suite.injectInSuiteContext(InjectableWithExtension.class,
				Singleton.of(InjectableInterfaceExtension.RELAYED_INJECTABLE_SINGLETON_ID, relayed));

		assertSame(relayed, injectable.extendedInjectable.explicitInjectable);
	}
}
