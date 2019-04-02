package com.mantledillusion.injection.hura.property;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.Blueprint.PropertyAllocation;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithPropertyAndSingleton;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

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
		
		assertEquals(propertyValue, injectable.propertyValue);
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
		
		assertEquals(propertyValue, injectable.propertyValue);
		assertSame(singleton, injectable.singleton);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultiplePropertyRootInjectorDefinition() {
		Injector.of(PropertyAllocation.of("key", "a"), PropertyAllocation.of("key", "b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultiplePropertyBlueprintDefinition() {
		Injector.of(PropertyAllocation.of("key", "a"), PropertyAllocation.of("key", "b"));
	}
}
