package com.mantledillusion.injection.hura.property;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprintTemplate;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
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
		
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithProperty>() {

			@Override
			public Class<InjectableWithProperty> getRootType() {
				return InjectableWithProperty.class;
			}
			
			@Define
			public Property propertyDefinitionMethod() {
				return Property.of("property.key", propertyValue);
			}
		}));
		
		assertEquals(propertyValue, injectable.propertyValue);
	}
	
	@Test
	public void testCollectivePredefinableDefinition() {
		String propertyValue = "value";
		String singleton = "singleton";
		
		InjectableWithPropertyAndSingleton injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithPropertyAndSingleton>() {

			@Override
			public Class<InjectableWithPropertyAndSingleton> getRootType() {
				return InjectableWithPropertyAndSingleton.class;
			}
			
			@Define
			public Set<Predefinable> propertyDefinitionMethod() {
				return new HashSet<>(Arrays.asList(Property.of("property.key", propertyValue), Singleton.of("qualifier", singleton)));
			}
		}));
		
		assertEquals(propertyValue, injectable.propertyValue);
		assertSame(singleton, injectable.singleton);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultiplePropertyRootInjectorDefinition() {
		Injector.of(Property.of("key", "a"), Property.of("key", "b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultiplePropertyBlueprintDefinition() {
		Blueprint.of(Property.of("key", "a"), Property.of("key", "b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultipleSingletonRootInjectorDefinition() {
		Injector.of(Singleton.of("id", "a"), Singleton.of("id", "b"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testMultipleSingletonBlueprintDefinition() {
		Blueprint.of(Singleton.of("id", "a"), Singleton.of("id", "b"));
	}
}
