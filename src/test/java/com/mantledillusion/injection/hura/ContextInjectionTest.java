package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.injectables.InjectableWithContextSensitivity;
import com.mantledillusion.injection.hura.misc.ExampleContext;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithBeanWithWiringContextSensitiveSingleton;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithContextWithoutSingletonId;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithGlobalSingletonContext;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithGlobalSingletonInjector;

public class ContextInjectionTest extends AbstractInjectionTest {

	@Test(expected=InjectionException.class)
	public void testDirectContextInjection() {
		this.suite.injectInSuiteContext(ExampleContext.class);
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
	public void testContextWithoutSingletonIdInjection() {
		this.suite.injectInSuiteContext(UninjectableWithContextWithoutSingletonId.class, new ExampleContext());
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
