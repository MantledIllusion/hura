package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import com.mantledillusion.injection.hura.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.injectables.InjectableWithAnnotatedConstructor;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjectableConstructor;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWith2InjectableConstructors;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWith2UseAnnotatedConstructors;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithExceptionThrowingConstructor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithFinalWiredField;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithIncompleteUseAnnotatedConstructor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithMissingUseAnnotationOnNonPublicNoArgsConstructor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithStaticWiredField;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithUninjectableConstructor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithWiredInterfaceField;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithWiredSelf;

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

	@Test(expected = ValidatorException.class)
	public void testStaticFieldInjection() {
		this.suite.injectInSuiteContext(UninjectableWithStaticWiredField.class);
	}

	@Test(expected = ValidatorException.class)
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

	@Test(expected = ValidatorException.class)
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
	public void testExplicitIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitIndependent.class);

		assertTrue(injectable.explicitInjectable == null);
	}
}
