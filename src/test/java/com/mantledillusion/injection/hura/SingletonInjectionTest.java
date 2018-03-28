package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mantledillusion.injection.hura.Injector.RootInjector;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.Predefinable.SingletonMapping;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;
import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.exception.MappingException;
import com.mantledillusion.injection.hura.injectables.Injectable;
import com.mantledillusion.injection.hura.injectables.InjectableWithExplicitSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithGlobalAndSequenceSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithGlobalSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjector;
import com.mantledillusion.injection.hura.injectables.InjectableWithSequenceSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithSequenceSingletonInjectables;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithWrongTypeSingleton;

public class SingletonInjectionTest extends AbstractInjectionTest {

	@Test
	public void testSequenceSingletonInjection() {
		InjectableWithSequenceSingletonInjectables injectable = this.suite
				.injectInSuiteContext(InjectableWithSequenceSingletonInjectables.class);

		assertSame(injectable.a.sequenceSingleton, injectable.b.sequenceSingleton);

		Injectable singleton = new Injectable();
		InjectableWithInjector main = this.suite.injectInSuiteContext(InjectableWithInjector.class,
				Singleton.of(InjectableWithSequenceSingleton.SINGLETON, singleton));
		InjectableWithSequenceSingleton childA = main.injector.instantiate(InjectableWithSequenceSingleton.class);
		InjectableWithSequenceSingleton childB = main.injector.instantiate(InjectableWithSequenceSingleton.class);

		assertSame(singleton, childA.sequenceSingleton);
		assertSame(singleton, childB.sequenceSingleton);
	}

	@Test
	public void testGlobalSingletonInjection() {
		Injectable injectable = new Injectable();
		Singleton globalSingleton = Singleton.of(InjectableWithGlobalSingleton.SINGLETON, injectable);

		RootInjector injector = Injector.of(globalSingleton);

		InjectableWithGlobalSingleton a = injector.instantiate(InjectableWithGlobalSingleton.class);
		InjectableWithGlobalSingleton b = injector.instantiate(InjectableWithGlobalSingleton.class);

		assertSame(injectable, a.globalSingleton);
		assertSame(injectable, b.globalSingleton);

		injector.destroyInjector();

		InjectableWithGlobalSingleton c = injector.instantiate(InjectableWithGlobalSingleton.class);

		assertNotSame(injectable, c.globalSingleton);

		InjectableWithGlobalAndSequenceSingleton d = this.suite
				.injectInRootContext(InjectableWithGlobalAndSequenceSingleton.class);

		assertNotSame(d.sequenceSingleton, d.globalSingleton.sequenceSingleton);
	}

	@Test
	public void testGlobalSingletonInjectionWithoutPredefinition() {
		Injector rootInjector = Injector.of();

		InjectableWithInjector injectable = rootInjector.instantiate(InjectableWithInjector.class);

		InjectableWithGlobalSingleton a = injectable.injector.instantiate(InjectableWithGlobalSingleton.class);
		InjectableWithGlobalSingleton b = rootInjector.instantiate(InjectableWithGlobalSingleton.class);

		assertSame(a.globalSingleton, b.globalSingleton);
	}

	@Test(expected = InjectionException.class)
	public void testWrongTypeSingletonInjection() {
		this.suite.injectInSuiteContext(UninjectableWithWrongTypeSingleton.class);
	}

	@Test
	public void testExplicitSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitSingleton.class);

		assertTrue(injectable.explicitInjectable == null);
	}

	@Test
	public void testSingletonMapping() {
		String singletonId = "theSingletonIdToMapTo";
		Injectable singleton = new Injectable();
		Injector rootInjector = Injector.of(Singleton.of(singletonId, singleton));

		InjectableWithGlobalSingleton injectable = rootInjector.instantiate(InjectableWithGlobalSingleton.class,
				SingletonMapping.of(InjectableWithGlobalSingleton.SINGLETON, singletonId, SingletonMode.GLOBAL));

		assertSame(singleton, injectable.globalSingleton);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDefineDoubleMapping() {
		Injector.of(SingletonMapping.of("1", "2", SingletonMode.GLOBAL),
				SingletonMapping.of("1", "3", SingletonMode.GLOBAL));
	}

	@Test(expected = MappingException.class)
	public void testDefineLoopedMappings() {
		Injector.of(SingletonMapping.of("1", "2", SingletonMode.GLOBAL),
				SingletonMapping.of("2", "3", SingletonMode.GLOBAL),
				SingletonMapping.of("3", "1", SingletonMode.GLOBAL));
	}
}
