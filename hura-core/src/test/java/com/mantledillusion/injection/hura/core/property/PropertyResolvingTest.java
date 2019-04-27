package com.mantledillusion.injection.hura.core.property;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.Injector.RootInjector;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.exception.ResolvingException;
import com.mantledillusion.injection.hura.core.property.injectables.*;
import com.mantledillusion.injection.hura.core.property.uninjectables.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyResolvingTest extends AbstractInjectionTest {

	@Test
	public void testPropertyFieldResolving() {
		String propertyValue = "value";
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(InjectableWithProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyConstructorResolving() {
		String propertyValue = "value";
		InjectableWithResolvableConstructor injectable = this.suite.injectInSuiteContext(
				InjectableWithResolvableConstructor.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyProcessingResolving() {
		String propertyValue = "value";
		InjectableWithProcessingResolver injectable = this.suite.injectInSuiteContext(
				InjectableWithProcessingResolver.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.propertyValue);
	}

	@Test
	public void testPropertyProcessorResolving() {
		String propertyValue = "value";
		InjectableWithProcessorResolver injectable = this.suite.injectInSuiteContext(
				InjectableWithProcessorResolver.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.propertyValue);
	}
	
	@Test(expected = ProcessorException.class)
	public void testMatchingWithoutProperty() {
		this.suite.injectInSuiteContext(UninjectableWithMatcherAndMissingProperty.class);
	}

	@Test
	public void testPropertyMatching() {
		String propertyValue = "23";
		InjectableWithMatchedProperty injectable = this.suite.injectInSuiteContext(InjectableWithMatchedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.exactly2NumbersPropertyValue);
	}

	@Test(expected = ResolvingException.class)
	public void testPropertyNonMatching() {
		String propertyValue = "non2DigitPropertyValue";
		this.suite.injectInSuiteContext(InjectableWithMatchedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));
	}
	
	@Test(expected = ResolvingException.class)
	public void testPropertyAnnotationMissingForDefaultValue() {
		this.suite.injectInSuiteContext(UninjectableWithDefaultValueMissingProperty.class);
	}
	
	@Test
	public void testOptionalPropertyWithDefaultValue() {
		InjectableWithOptionalPropertyAndDefaultValue injectable = this.suite.injectInSuiteContext(InjectableWithOptionalPropertyAndDefaultValue.class);
		assertEquals(InjectableWithOptionalPropertyAndDefaultValue.DEFAULT_VALUE, injectable.property);
	}

	@Test(expected = ResolvingException.class)
	public void testPropertyMatchingWithDefault() {
		String propertyValue = "non2DigitPropertyValue";
		InjectableWithMatchedDefaultedProperty injectable = this.suite.injectInSuiteContext(
				InjectableWithMatchedDefaultedProperty.class, Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(InjectableWithMatchedDefaultedProperty.DEFAULT_VALUE, injectable.exactly2NumbersPropertyValue);
	}

	@Test
	public void testPropertyNonResolving() {
		InjectableWithProperty injectable = this.suite.injectInSuiteContext(InjectableWithProperty.class);

		assertEquals("property.key", injectable.propertyValue);
	}

	@Test
	public void testDefaultedPropertyNonResolving() {
		InjectableWithDefaultedProperty injectable = this.suite
				.injectInSuiteContext(InjectableWithDefaultedProperty.class);

		assertEquals("defaultValue", injectable.propertyValue);
	}

	@Test
	public void testPropertyForcedResolving() {
		String propertyValue = "value";
		InjectableWithForcedProperty injectable = this.suite.injectInSuiteContext(InjectableWithForcedProperty.class,
				Blueprint.PropertyAllocation.of("property.key", propertyValue));

		assertEquals(propertyValue, injectable.forcedPropertyValue);
	}

	@Test(expected = ResolvingException.class)
	public void testPropertyForcedNonResolving() {
		this.suite.injectInSuiteContext(InjectableWithForcedProperty.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKeylessProperty() {
		Blueprint.PropertyAllocation.of("", "someValue");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValuelessProperty() {
		Blueprint.PropertyAllocation.of("property.key", null);
	}

	@Test(expected = ProcessorException.class)
	public void testKeylessPropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithoutPropertyKey.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test(expected = ProcessorException.class)
	public void testNonStringPropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithNonStringPropertyField.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test(expected = ProcessorException.class)
	public void testStaticPropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithStaticProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test(expected = ProcessorException.class)
	public void testFinalPropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithFinalProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test(expected = ProcessorException.class)
	public void testUnparsableMatcherPropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithUnparsableMatcherProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test(expected = ResolvingException.class)
	public void testUnmatchingDefaultValuePropertyResolving() {
		this.suite.injectInSuiteContext(UninjectableWithUnmatchingDefaultValueProperty.class,
				Blueprint.PropertyAllocation.of("property.key", "unneededValue"));
	}

	@Test
	public void testPropertyPredefiningAndOverriding() {
		String predefined = "predefined";
		RootInjector injector = Injector.of(Blueprint.PropertyAllocation.of("property.key", predefined));

		InjectableWithProperty injectable = injector.instantiate(InjectableWithProperty.class);
		assertEquals(predefined, injectable.propertyValue);

		String overridden = "overridden";
		injectable = injector.instantiate(InjectableWithProperty.class, Blueprint.PropertyAllocation.of("property.key", overridden));
		assertEquals(overridden, injectable.propertyValue);
	}
}
