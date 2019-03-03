package com.mantledillusion.injection.hura.context;

import static org.junit.Assert.assertTrue;

import com.mantledillusion.injection.hura.AbstractInjectionTest;
import org.junit.Test;

import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.context.injectables.InjectableWithContextSensitivity;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;
import com.mantledillusion.injection.hura.context.uninjectables.UninjectableWithBeanWithWiringContextSensitiveSingleton;
import com.mantledillusion.injection.hura.context.uninjectables.UninjectableWithContextWithoutQualifier;
import com.mantledillusion.injection.hura.context.uninjectables.UninjectableWithGlobalSingletonContext;
import com.mantledillusion.injection.hura.context.uninjectables.UninjectableWithGlobalSingletonInjector;
import com.mantledillusion.injection.hura.context.uninjectables.UninjectableWithSequenceSingletonInjector;

public class ContextInjectionTest extends AbstractInjectionTest {

	@Test(expected=InjectionException.class)
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
	
	@Test(expected=InjectionException.class)
	public void testContextSensitiveSingletonInjection() {
		this.suite.injectInSuiteContext(UninjectableWithBeanWithWiringContextSensitiveSingleton.class, new ExampleContext());
	}
	
	@Test
	public void testContextInjection() {
		ExampleContext context = new ExampleContext();
		InjectableWithContextSensitivity injectable = this.suite.injectInSuiteContext(InjectableWithContextSensitivity.class, context);
		
		assertTrue(injectable.context == context);
	}
}
