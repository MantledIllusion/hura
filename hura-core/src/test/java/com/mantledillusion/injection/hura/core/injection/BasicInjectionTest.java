package com.mantledillusion.injection.hura.core.injection;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithAnnotatedConstructor;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableConstructor;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.core.injection.uninjectables.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BasicInjectionTest extends AbstractInjectionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNullInjection() {
		this.suite.injectInSuiteContext((Class<?>) null);
	}

	@Test
	public void testBasicFieldInjection() {
		InjectableWithInjectableField injectable = this.suite.injectInSuiteContext(InjectableWithInjectableField.class);

		assertTrue(injectable.wiredField != null);
	}

	@Test(expected = InjectionException.class)
	public void testExceptionThrowingConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithExceptionThrowingConstructor.class);
	}

	@Test(expected = ProcessorException.class)
	public void testStaticFieldInjection() {
		this.suite.injectInSuiteContext(UninjectableWithStaticWiredField.class);
	}

	@Test(expected = ProcessorException.class)
	public void testFinalFieldInjection() {
		this.suite.injectInSuiteContext(UninjectableWithFinalWiredField.class);
	}

	@Test
	public void testBasicConstructorInjection() {
		InjectableWithInjectableConstructor injectable = this.suite
				.injectInSuiteContext(InjectableWithInjectableConstructor.class);

		assertTrue(injectable.wiredThroughConstructor != null);
	}

	@Test(expected = InjectionException.class)
	public void testUninjectableConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithUninjectableConstructor.class);
	}

	@Test(expected = InjectionException.class)
	public void testMultipleInjectableConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWith2InjectableConstructors.class);
	}

	@Test
	public void testUseAnnotatedConstructorInjection() {
		InjectableWithAnnotatedConstructor injectable = this.suite
				.injectInSuiteContext(InjectableWithAnnotatedConstructor.class);

		assertTrue(injectable.onlyWiredThroughUseAnnotatedConstructor != null);
	}

	@Test(expected = InjectionException.class)
	public void testMultipleUseAnnotatedConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWith2UseAnnotatedConstructors.class);
	}

	@Test(expected = ProcessorException.class)
	public void testIncompleteUseAnnotatedConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithIncompleteUseAnnotatedConstructor.class);
	}

	@Test(expected = InjectionException.class)
	public void testMissingUseAnnotationOnNonPublicNoArgsConstructorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithMissingUseAnnotationOnNonPublicNoArgsConstructor.class);
	}

	@Test(expected = InjectionException.class)
	public void testInterfaceInjection() {
		this.suite.injectInSuiteContext(UninjectableWithWiredInterfaceField.class);
	}

	@Test(expected = InjectionException.class)
	public void testSelfInjection() {
		this.suite.injectInSuiteContext(UninjectableWithWiredSelf.class);
	}

	@Test
	public void testOptionalIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitIndependent.class);

		assertTrue(injectable.explicitInjectable == null);
	}
	
	@Test(expected = ProcessorException.class)
	public void testOptionalIndependentInjectionWithMissingInjectAnnotation() {
		this.suite.injectInSuiteContext(UninjectableWithOptionalInjectableAndMissingInjectAnnotation.class);
	}
}
