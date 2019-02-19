package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprintTemplate;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.annotation.Define;
import com.mantledillusion.injection.hura.annotation.Global.SingletonMode;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import com.mantledillusion.injection.hura.injectables.InjectableWithMappingAdjustment;
import com.mantledillusion.injection.hura.injectables.Injectable;
import com.mantledillusion.injection.hura.injectables.Injectable2;
import com.mantledillusion.injection.hura.injectables.InjectableWithExtendingAdjustment;
import com.mantledillusion.injection.hura.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.injectables.InjectableWithPropertyAdjustment;
import com.mantledillusion.injection.hura.injectables.InjectableWithSequenceSingleton;
import com.mantledillusion.injection.hura.misc.InjectableInterface;
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
				.injectInSuiteContext(InjectableWithPropertyAdjustment.class, 
						Property.of(InjectableWithProperty.PROPERTY_KEY, "default value"));

		assertEquals(InjectableWithPropertyAdjustment.ADJUSTED_PROPERTY_VALUE,
				injectable.propertiedInjectable.propertyValue);
	}

	@Test
	public void testMappingAdjustment() {
		Injectable singleton = new Injectable();
		InjectableWithMappingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectableWithMappingAdjustment.class,
				Singleton.of(InjectableWithMappingAdjustment.SOME_QUALIFIER, singleton),
				Mapping.of(InjectableWithSequenceSingleton.SINGLETON, "predefinitionQualifier", SingletonMode.SEQUENCE));
		
		assertSame(singleton, injectable.singletonedInjectable.sequenceSingleton);
	}

	@Test
	public void testExtendingAdjustment() {
		InjectableWithExtendingAdjustment injectable = this.suite.injectInSuiteContext(Blueprint.from(new TypedBlueprintTemplate<InjectableWithExtendingAdjustment>() {

			@Override
			public Class<InjectableWithExtendingAdjustment> getRootType() {
				return InjectableWithExtendingAdjustment.class;
			}
			
			@Define
			private BeanAllocation<InjectableInterface> allocate() {
				return BeanAllocation.allocateToType(Injectable.class);
			}
			
		}));

		assertTrue(injectable.extendedInjectable.explicitInjectable instanceof Injectable2);
	}
}
