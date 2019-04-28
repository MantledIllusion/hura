package com.mantledillusion.injection.hura.core.injection;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithAnnotatedConstructor;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableConstructor;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.core.injection.uninjectables.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BasicInjectionTest extends AbstractInjectionTest {

	@Test
	public void testNullInjection() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.suite.injectInSuiteContext((Class<?>) null));
	}

	@Test
	public void testBasicFieldInjection() {
		InjectableWithInjectableField injectable = this.suite.injectInSuiteContext(InjectableWithInjectableField.class);

		Assertions.assertTrue(injectable.wiredField != null);
	}

	@Test
	public void testExceptionThrowingConstructorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithExceptionThrowingConstructor.class));
	}

	@Test
	public void testStaticFieldInjection() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithStaticWiredField.class));
	}

	@Test
	public void testFinalFieldInjection() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithFinalWiredField.class));
	}

	@Test
	public void testBasicConstructorInjection() {
		InjectableWithInjectableConstructor injectable = this.suite
				.injectInSuiteContext(InjectableWithInjectableConstructor.class);

		Assertions.assertTrue(injectable.wiredThroughConstructor != null);
	}

	@Test
	public void testUninjectableConstructorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithUninjectableConstructor.class));
	}

	@Test
	public void testMultipleInjectableConstructorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWith2InjectableConstructors.class));
	}

	@Test
	public void testUseAnnotatedConstructorInjection() {
		InjectableWithAnnotatedConstructor injectable = this.suite
				.injectInSuiteContext(InjectableWithAnnotatedConstructor.class);

		Assertions.assertTrue(injectable.onlyWiredThroughUseAnnotatedConstructor != null);
	}

	@Test
	public void testMultipleUseAnnotatedConstructorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWith2UseAnnotatedConstructors.class));
	}

	@Test
	public void testIncompleteUseAnnotatedConstructorInjection() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithIncompleteUseAnnotatedConstructor.class));
	}

	@Test
	public void testMissingUseAnnotationOnNonPublicNoArgsConstructorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithMissingUseAnnotationOnNonPublicNoArgsConstructor.class));
	}

	@Test
	public void testInterfaceInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithWiredInterfaceField.class));
	}

	@Test
	public void testSelfInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithWiredSelf.class));
	}

	@Test
	public void testOptionalIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitIndependent.class);

		Assertions.assertTrue(injectable.explicitInjectable == null);
	}
	
	@Test
	public void testOptionalIndependentInjectionWithMissingInjectAnnotation() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithOptionalInjectableAndMissingInjectAnnotation.class));
	}
}
