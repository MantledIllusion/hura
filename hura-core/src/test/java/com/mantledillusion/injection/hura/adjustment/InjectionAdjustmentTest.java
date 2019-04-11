package com.mantledillusion.injection.hura.adjustment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import org.junit.Test;

import com.mantledillusion.injection.hura.adjustment.injectables.InjectableWithMappingAdjustment;
import com.mantledillusion.injection.hura.adjustment.injectables.InjectableAlternative;
import com.mantledillusion.injection.hura.adjustment.injectables.InjectableWithExtendingAdjustment;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.adjustment.injectables.InjectableWithPropertyAdjustment;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSequenceSingleton;
import com.mantledillusion.injection.hura.adjustment.uninjectables.UninjectableWithInjectionlessAdjustment;
import com.mantledillusion.injection.hura.adjustment.uninjectables.UninjectableWithSingletonAdjustment;

public class InjectionAdjustmentTest extends AbstractInjectionTest {

	@Test(expected = ProcessorException.class)
	public void testAdjustmentWithoutInjection() {
		this.suite.injectInSuiteContext(UninjectableWithInjectionlessAdjustment.class);
	}

	@Test(expected = ProcessorException.class)
	public void testAdjustmentOnSingleton() {
		this.suite.injectInSuiteContext(UninjectableWithSingletonAdjustment.class);
	}
	
	@Test
	public void testPropertyAdjustment() {
		InjectableWithPropertyAdjustment injectable = this.suite
				.injectInSuiteContext(InjectableWithPropertyAdjustment.class, 
						Blueprint.PropertyAllocation.of(InjectableWithProperty.PROPERTY_KEY, "default value"));

		assertEquals(InjectableWithPropertyAdjustment.ADJUSTED_PROPERTY_VALUE,
				injectable.propertiedInjectable.propertyValue);
	}

	@Test
	public void testMappingAdjustment() {
		Injectable singleton = new Injectable();
		InjectableWithMappingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectableWithMappingAdjustment.class,
				Blueprint.SingletonAllocation.of(InjectableWithMappingAdjustment.SOME_QUALIFIER, singleton),
				Blueprint.MappingAllocation.of(InjectableWithSequenceSingleton.SINGLETON, "predefinitionQualifier"));
		
		assertSame(singleton, injectable.singletonedInjectable.sequenceSingleton);
	}

	@Test
	public void testExtendingAdjustment() {
		InjectableWithExtendingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectableWithExtendingAdjustment.class,
				new Blueprint() {

					@Define
					TypeAllocation allocate() {
						return Blueprint.TypeAllocation.allocateToType(InjectableInterface.class, Injectable.class);
					}
				});

		assertTrue(injectable.extendedInjectable.explicitInjectable instanceof InjectableAlternative);
	}
}
