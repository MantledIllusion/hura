package com.mantledillusion.injection.hura.property;

import static org.junit.Assert.assertEquals;

import com.mantledillusion.injection.hura.AbstractInjectionTest;
import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import org.junit.Test;

import com.mantledillusion.injection.hura.Injector.RootInjector;
import com.mantledillusion.injection.hura.exception.ResolvingException;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithResolvableConstructor;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithUnparsableMatcherProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithDefaultValueMissingProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithFinalProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithMatcherAndMissingProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithNonStringPropertyField;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithOptionalPropertyAndDefaultValue;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithStaticProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithUnmatchingDefaultValueProperty;
import com.mantledillusion.injection.hura.property.uninjectables.UninjectableWithoutPropertyKey;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProcessorResolver;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithDefaultedProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithForcedProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithMatchedDefaultedProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithMatchedProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProcessingResolver;

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
	
	@Test(expected = ProcessorException.class)
	public void testPropertyAnnotationMissingForDefaultValue() {
		this.suite.injectInSuiteContext(UninjectableWithDefaultValueMissingProperty.class);
	}
	
	@Test(expected = ProcessorException.class)
	public void testOptionalPropertyWithDefaultValue() {
		this.suite.injectInSuiteContext(UninjectableWithOptionalPropertyAndDefaultValue.class);
	}

	@Test
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

	@Test(expected = ProcessorException.class)
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
