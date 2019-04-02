package com.mantledillusion.injection.hura.singleton;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.mantledillusion.injection.hura.AbstractInjectionTest;
import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import org.junit.Test;

import com.mantledillusion.injection.hura.Blueprint.SingletonAllocation;
import com.mantledillusion.injection.hura.exception.InjectionException;
import com.mantledillusion.injection.hura.exception.MappingException;
import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithExplicitAndEagerSingleton;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithExplicitSingleton;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithInjector;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSequenceSingleton;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSequenceSingletonInjectables;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSingletonAllocationRequired;
import com.mantledillusion.injection.hura.singleton.uninjectables.UninjectableWithSingletonWithoutInject;
import com.mantledillusion.injection.hura.singleton.uninjectables.UninjectableWithWrongTypeSingleton;

public class SingletonInjectionTest extends AbstractInjectionTest {

	@Test
	public void testSequenceSingletonInjection() {
		InjectableWithSequenceSingletonInjectables injectable = this.suite
				.injectInSuiteContext(InjectableWithSequenceSingletonInjectables.class);

		assertSame(injectable.a.sequenceSingleton, injectable.b.sequenceSingleton);

		Injectable singleton = new Injectable();
		InjectableWithInjector main = this.suite.injectInSuiteContext(InjectableWithInjector.class,
				SingletonAllocation.of(InjectableWithSequenceSingleton.SINGLETON, singleton));
		InjectableWithSequenceSingleton childA = main.injector.instantiate(InjectableWithSequenceSingleton.class);
		InjectableWithSequenceSingleton childB = main.injector.instantiate(InjectableWithSequenceSingleton.class);

		assertSame(singleton, childA.sequenceSingleton);
		assertSame(singleton, childB.sequenceSingleton);
	}
	
	@Test(expected = ProcessorException.class)
	public void testSingletonInjectionWithoutInject() {
		this.suite.injectInSuiteContext(UninjectableWithSingletonWithoutInject.class);
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
	public void testBothExplicitAndEagerSingletonInjection() {
		InjectableWithExplicitAndEagerSingleton injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitAndEagerSingleton.class);

		assertTrue(injectable.eagerInjectable != null);
		assertTrue(injectable.explicitInjectable == null);
	}

	@Test
	public void testDifferentTypeSingletonInjectionWithAllocation() {
		Injectable bean = new Injectable();
		SingletonAllocation singleton = Blueprint.SingletonAllocation.of(InjectableWithSingletonAllocationRequired.SINGLETON, bean);

		InjectableWithSingletonAllocationRequired injectable = this.suite
				.injectInSuiteContext(InjectableWithSingletonAllocationRequired.class, singleton);

		assertSame(bean, injectable.implSingleton);
		assertSame(bean, injectable.interfaceSingleton);
	}

	@Test(expected = InjectionException.class)
	public void testDifferentTypeSingletonInjectionWithoutAllocation() {
		this.suite.injectInRootContext(InjectableWithSingletonAllocationRequired.class);
	}

	@Test
	public void testSingletonMapping() {
		String qualifier = "theQualifierToMapTo";
		Injectable singleton = new Injectable();
		Injector rootInjector = Injector.of(SingletonAllocation.of(qualifier, singleton));

		InjectableWithSequenceSingleton injectable = rootInjector.instantiate(InjectableWithSequenceSingleton.class,
				Blueprint.MappingAllocation.of(InjectableWithSequenceSingleton.SINGLETON, qualifier));

		assertSame(singleton, injectable.sequenceSingleton);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDefineDoubleMapping() {
		Injector.of(Blueprint.MappingAllocation.of("1", "2"), Blueprint.MappingAllocation.of("1", "3"));
	}

	@Test(expected = MappingException.class)
	public void testDefineLoopedMappings() {
		Injector.of(Blueprint.MappingAllocation.of("1", "2"), Blueprint.MappingAllocation.of("2", "3"),
				Blueprint.MappingAllocation.of("3", "1"));
	}
}
