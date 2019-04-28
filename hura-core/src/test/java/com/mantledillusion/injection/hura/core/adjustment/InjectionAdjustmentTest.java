package com.mantledillusion.injection.hura.core.adjustment;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.adjustment.injectables.InjectableAlternative;
import com.mantledillusion.injection.hura.core.adjustment.injectables.InjectableWithExtendingAdjustment;
import com.mantledillusion.injection.hura.core.adjustment.injectables.InjectableWithMappingAdjustment;
import com.mantledillusion.injection.hura.core.adjustment.injectables.InjectableWithPropertyAdjustment;
import com.mantledillusion.injection.hura.core.adjustment.uninjectables.UninjectableWithInjectionlessAdjustment;
import com.mantledillusion.injection.hura.core.adjustment.uninjectables.UninjectableWithSingletonAdjustment;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.core.singleton.injectables.InjectableWithSequenceSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InjectionAdjustmentTest extends AbstractInjectionTest {

	@Test
	public void testAdjustmentWithoutInjection() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithInjectionlessAdjustment.class));
	}

	@Test
	public void testAdjustmentOnSingleton() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithSingletonAdjustment.class));
	}
	
	@Test
	public void testPropertyAdjustment() {
		InjectableWithPropertyAdjustment injectable = this.suite
				.injectInSuiteContext(InjectableWithPropertyAdjustment.class, 
						Blueprint.PropertyAllocation.of(InjectableWithProperty.PROPERTY_KEY, "default value"));

		Assertions.assertEquals(InjectableWithPropertyAdjustment.ADJUSTED_PROPERTY_VALUE,
				injectable.propertiedInjectable.propertyValue);
	}

	@Test
	public void testMappingAdjustment() {
		Injectable singleton = new Injectable();
		InjectableWithMappingAdjustment injectable = this.suite.injectInSuiteContext(
				InjectableWithMappingAdjustment.class,
				Blueprint.SingletonAllocation.of(InjectableWithMappingAdjustment.SOME_QUALIFIER, singleton),
				Blueprint.MappingAllocation.of(InjectableWithSequenceSingleton.SINGLETON, "predefinitionQualifier"));
		
		Assertions.assertSame(singleton, injectable.singletonedInjectable.sequenceSingleton);
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

		Assertions.assertTrue(injectable.extendedInjectable.explicitInjectable instanceof InjectableAlternative);
	}
}
