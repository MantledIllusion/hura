package com.mantledillusion.injection.hura.core.property;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.Injector.RootInjector;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.exception.ResolvingException;
import com.mantledillusion.injection.hura.core.property.injectables.*;
import com.mantledillusion.injection.hura.core.property.uninjectables.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PropertyResolvingTest extends AbstractInjectionTest {

	@Test
	public void testPropertyFieldResolving() {
		String propertyValue = "value";
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(InjectableWithProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyConstructorResolving() {
		String propertyValue = "value";
		InjectableWithResolvableConstructor injectable = this.suite.injectInSuiteContext(
				InjectableWithResolvableConstructor.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyProcessingResolving() {
		String propertyValue = "value";
		InjectableWithProcessingResolver injectable = this.suite.injectInSuiteContext(
				InjectableWithProcessingResolver.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyProcessorResolving() {
		String propertyValue = "value";
		InjectableWithProcessorResolver injectable = this.suite.injectInSuiteContext(
				InjectableWithProcessorResolver.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.propertyValue);
	}
	
	@Test
	public void testMatchingWithoutProperty() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithMatcherAndMissingProperty.class));
	}

	@Test
	public void testPropertyMatching() {
		String propertyValue = "23";
		InjectableWithMatchedProperty injectable = this.suite.injectInSuiteContext(InjectableWithMatchedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.exactly2NumbersPropertyValue);
	}

	@Test
	public void testPropertyNonMatching() {
		String propertyValue = "non2DigitPropertyValue";
		Assertions.assertThrows(ResolvingException.class, () -> this.suite.injectInSuiteContext(InjectableWithMatchedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue)));
	}
	
	@Test
	public void testPropertyAnnotationMissingForDefaultValue() {
		Assertions.assertThrows(ResolvingException.class, () -> this.suite.injectInSuiteContext(UninjectableWithDefaultValueMissingProperty.class));
	}
	
	@Test
	public void testOptionalPropertyWithDefaultValue() {
		InjectableWithOptionalPropertyAndDefaultValue injectable = this.suite.injectInSuiteContext(InjectableWithOptionalPropertyAndDefaultValue.class);
		Assertions.assertEquals(InjectableWithOptionalPropertyAndDefaultValue.DEFAULT_VALUE, injectable.property);
	}

	@Test
	public void testPropertyMatchingWithDefault() {
		String propertyValue = "non2DigitPropertyValue";

		Assertions.assertThrows(ResolvingException.class, () -> this.suite.injectInSuiteContext(
				InjectableWithMatchedDefaultedProperty.class, Blueprint.PropertyAllocation.of("property.key", propertyValue)));
	}

	@Test
	public void testPropertyNonResolving() {
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(InjectableWithProperty.class);

		Assertions.assertEquals("property.key", injectable.propertyValue);
	}

	@Test
	public void testDefaultedPropertyNonResolving() {
		InjectableWithDefaultedProperty injectable = this.suite
				.injectInSuiteContext(InjectableWithDefaultedProperty.class);

		Assertions.assertEquals("defaultValue", injectable.propertyValue);
	}

	@Test
	public void testPropertyForcedResolving() {
		String propertyValue = "value";
		InjectableWithForcedProperty injectable = this.suite.injectInSuiteContext(InjectableWithForcedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		Assertions.assertEquals(propertyValue, injectable.forcedPropertyValue);
	}

	@Test
	public void testPropertyForcedNonResolving() {
		Assertions.assertThrows(ResolvingException.class, () -> this.suite.injectInSuiteContext(InjectableWithForcedProperty.class));
	}

	@Test
	public void testKeylessProperty() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> Blueprint.PropertyAllocation.of("", "someValue"));
	}

	@Test
	public void testValuelessProperty() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> Blueprint.PropertyAllocation.of("property.key", null));
	}

	@Test
	public void testKeylessPropertyResolving() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithoutPropertyKey.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testNonStringPropertyResolving() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithNonStringPropertyField.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testStaticPropertyResolving() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithStaticProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testFinalPropertyResolving() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithFinalProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testUnparsableMatcherPropertyResolving() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithUnparsableMatcherProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testUnmatchingDefaultValuePropertyResolving() {
		Assertions.assertThrows(ResolvingException.class, () -> this.suite.injectInSuiteContext(UninjectableWithUnmatchingDefaultValueProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue")));
	}

	@Test
	public void testPropertyPredefiningAndOverriding() {
		String predefined = "predefined";
		RootInjector injector = Injector.of(Blueprint.PropertyAllocation.of("property.key", predefined));

		InjectableWithProperty injectable = injector.instantiate(InjectableWithProperty.class);
		Assertions.assertEquals(predefined, injectable.propertyValue);

		String overridden = "overridden";
		injectable = injector.instantiate(InjectableWithProperty.class, Blueprint.PropertyAllocation.of("property.key", overridden));
		Assertions.assertEquals(overridden, injectable.propertyValue);
	}
}
