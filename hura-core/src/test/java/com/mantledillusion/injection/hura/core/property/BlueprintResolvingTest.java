package com.mantledillusion.injection.hura.core.property;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Blueprint.PropertyAllocation;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithPropertyAndSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlueprintResolvingTest extends AbstractInjectionTest {
	
	@Test
	public void testBlueprintPropertyDefinition() {
		String propertyValue = "value";
		
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(InjectableWithProperty.class, new Blueprint() {
			
			@Define
			public PropertyAllocation propertyDefinitionMethod() {
				return PropertyAllocation.of("property.key", propertyValue);
			}
		});
		
		Assertions.assertEquals(propertyValue, injectable.propertyValue);
	}
	
	@Test
	public void testCollectivePredefinableDefinition() {
		String propertyValue = "value";
		String singleton = "singleton";
		
		InjectableWithPropertyAndSingleton injectable = this.suite.injectInSuiteContext(InjectableWithPropertyAndSingleton.class, new Blueprint() {
			
			@Define
			public Set<Blueprint.Allocation> propertyDefinitionMethod() {
				return new HashSet<>(Arrays.asList(PropertyAllocation.of("property.key", propertyValue), Blueprint.SingletonAllocation.of("qualifier", singleton)));
			}
		});

		Assertions.assertEquals(propertyValue, injectable.propertyValue);
		Assertions.assertSame(singleton, injectable.singleton);
	}
	
	@Test
	public void testMultiplePropertyRootInjectorDefinition() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> Injector.of(PropertyAllocation.of("key", "a"), PropertyAllocation.of("key", "b")));
	}
	
	@Test
	public void testMultiplePropertyBlueprintDefinition() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> Injector.of(PropertyAllocation.of("key", "a"), PropertyAllocation.of("key", "b")));
	}
}
