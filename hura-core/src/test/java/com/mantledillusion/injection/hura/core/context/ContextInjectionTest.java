package com.mantledillusion.injection.hura.core.context;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.context.injectables.InjectableWithContextSensitivity;
import com.mantledillusion.injection.hura.core.context.misc.ExampleContext;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithContextWithoutQualifier;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithGlobalSingletonContext;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithGlobalSingletonInjector;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithSequenceSingletonInjector;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContextInjectionTest extends AbstractInjectionTest {

	@Test
	public void testDirectContextInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(ExampleContext.class));
	}

	@Test
	public void testSequenceInjectorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithSequenceSingletonInjector.class));
	}

	@Test
	public void testGlobalInjectorInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithGlobalSingletonInjector.class));
	}

	@Test
	public void testGlobalContextInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithGlobalSingletonContext.class));
	}

	@Test
	public void testContextWithoutQualifierInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithContextWithoutQualifier.class, new ExampleContext()));
	}
	
	@Test
	public void testContextInjection() {
		ExampleContext context = new ExampleContext();
		InjectableWithContextSensitivity injectable = this.suite.injectInSuiteContext(InjectableWithContextSensitivity.class, context);
		
		Assertions.assertTrue(injectable.context == context);
	}
}
