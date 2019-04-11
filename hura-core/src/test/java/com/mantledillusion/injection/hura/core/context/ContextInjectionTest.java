package com.mantledillusion.injection.hura.core.context;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.context.injectables.InjectableWithContextSensitivity;
import com.mantledillusion.injection.hura.core.context.misc.ExampleContext;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithContextWithoutQualifier;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithGlobalSingletonContext;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithGlobalSingletonInjector;
import com.mantledillusion.injection.hura.core.context.uninjectables.UninjectableWithSequenceSingletonInjector;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ContextInjectionTest extends AbstractInjectionTest {

	@Test(expected= InjectionException.class)
	public void testDirectContextInjection() {
		this.suite.injectInSuiteContext(ExampleContext.class);
	}

	@Test(expected=InjectionException.class)
	public void testSequenceInjectorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithSequenceSingletonInjector.class);
	}

	@Test(expected=InjectionException.class)
	public void testGlobalInjectorInjection() {
		this.suite.injectInSuiteContext(UninjectableWithGlobalSingletonInjector.class);
	}

	@Test(expected=InjectionException.class)
	public void testGlobalContextInjection() {
		this.suite.injectInSuiteContext(UninjectableWithGlobalSingletonContext.class);
	}

	@Test(expected=InjectionException.class)
	public void testContextWithoutQualifierInjection() {
		this.suite.injectInSuiteContext(UninjectableWithContextWithoutQualifier.class, new ExampleContext());
	}
	
	@Test
	public void testContextInjection() {
		ExampleContext context = new ExampleContext();
		InjectableWithContextSensitivity injectable = this.suite.injectInSuiteContext(InjectableWithContextSensitivity.class, context);
		
		assertTrue(injectable.context == context);
	}
}
