package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import com.mantledillusion.injection.hura.injectables.InjectabeWithMappingAdjustment;
import com.mantledillusion.injection.hura.injectables.Injectable;
import com.mantledillusion.injection.hura.injectables.InjectableWithExtendingAdjustment;
import com.mantledillusion.injection.hura.injectables.InjectableWithPropertyAdjustment;
import com.mantledillusion.injection.hura.misc.InjectableInterfaceExtension;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithInjectionlessAdjustment;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithSingletonAdjustment;

public class InjectionAdjustmentTest extends AbstractInjectionTest {

	@Test(expected = ValidatorException.class)
	public void testAdjustmentWithoutInjection() {
		this.suite.injectInSuiteContext(UninjectableWithInjectionlessAdjustment.class);
	}

	@Test(expected = ValidatorException.class)
	public void testAdjustmentOnSingleton() {
		this.suite.injectInSuiteContext(UninjectableWithSingletonAdjustment.class);
	}
	
	@Test
	public void testPropertyAdjustment() {
		InjectableWithPropertyAdjustment injectable = this.suite
				.injectInSuiteContext(InjectableWithPropertyAdjustment.class);

		assertEquals(InjectableWithPropertyAdjustment.ADJUSTED_PROPERTY_VALUE,
				injectable.propertiedInjectable.propertyValue);
	}

	@Test
	public void testMappingAdjustment() {
		Injectable singleton = new Injectable();
		InjectabeWithMappingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectabeWithMappingAdjustment.class,
				Singleton.of(InjectabeWithMappingAdjustment.SOME_QUALIFIER, singleton));
		
		assertSame(singleton, injectable.singletonedInjectable.sequenceSingleton);
	}

	@Test
	public void testExtendingAdjustment() {
		Injectable relayed = new Injectable();

		InjectableWithExtendingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectableWithExtendingAdjustment.class,
				Singleton.of(InjectableInterfaceExtension.RELAYED_INJECTABLE_SINGLETON_ID, relayed));

		assertSame(relayed, injectable.extendedInjectable.explicitInjectable);
	}
}
