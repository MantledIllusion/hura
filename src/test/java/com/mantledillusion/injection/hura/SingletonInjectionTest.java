package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.injectables.Injectable;
import com.mantledillusion.injection.hura.injectables.InjectableWithExplicitSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithGlobalSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjector;
import com.mantledillusion.injection.hura.injectables.InjectableWithSequenceSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithSequenceSingletonInjectables;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithWrongTypeSingleton;

public class SingletonInjectionTest extends AbstractInjectionTest {

	@Test
	public void testSequenceSingletonInjection() {
		InjectableWithSequenceSingletonInjectables injectable = this.suite.injectInSuiteContext(InjectableWithSequenceSingletonInjectables.class);
		
		assertSame(injectable.a.sequenceSingleton, injectable.b.sequenceSingleton);
		
		Injectable singleton = new Injectable();
		InjectableWithInjector main = this.suite.injectInSuiteContext(InjectableWithInjector.class, Singleton.of(InjectableWithSequenceSingleton.SINGLETON, singleton));
		InjectableWithSequenceSingleton childA = main.injector.instantiate(InjectableWithSequenceSingleton.class);
		InjectableWithSequenceSingleton childB = main.injector.instantiate(InjectableWithSequenceSingleton.class);
		
		assertSame(singleton, childA.sequenceSingleton);
		assertSame(singleton, childB.sequenceSingleton);
	}
	
	@Test
	public void testGlobalSingletonInjection() {
		Injectable injectable = new Injectable();
		Singleton globalSingleton = Singleton.of(InjectableWithGlobalSingleton.SINGLETON, injectable);
		
		Injector injector = Injector.of(globalSingleton);
		
		InjectableWithGlobalSingleton a = injector.instantiate(InjectableWithGlobalSingleton.class);
		InjectableWithGlobalSingleton b = injector.instantiate(InjectableWithGlobalSingleton.class);
		
		assertSame(injectable, a.globalSingleton);
		assertSame(injectable, b.globalSingleton);
	}
	
	@Test
	public void testGlobalSingletonInjectionWithoutPredefinition() {
		Injector rootInjector = Injector.of();
		
		InjectableWithInjector injectable = rootInjector.instantiate(InjectableWithInjector.class);
		
		InjectableWithGlobalSingleton a = injectable.injector.instantiate(InjectableWithGlobalSingleton.class);
		InjectableWithGlobalSingleton b = rootInjector.instantiate(InjectableWithGlobalSingleton.class);
		
		assertSame(a.globalSingleton, b.globalSingleton);
	}
	
	@Test(expected=InjectionException.class)
	public void testWrongTypeSingletonInjection() {
		this.suite.injectInSuiteContext(UninjectableWithWrongTypeSingleton.class);
	}
	
	@Test
	public void testExplicitSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class);
		
		assertTrue(injectable.explicitInjectable == null);
	}
}
