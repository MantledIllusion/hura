package com.mantledillusion.injection.hura.core.singleton;

import com.mantledillusion.injection.hura.core.*;
import com.mantledillusion.injection.hura.core.Blueprint.SingletonAllocation;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import com.mantledillusion.injection.hura.core.exception.MappingException;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.singleton.injectables.*;
import com.mantledillusion.injection.hura.core.singleton.uninjectables.UninjectableWithSingletonWithoutInject;
import com.mantledillusion.injection.hura.core.singleton.uninjectables.UninjectableWithWrongTypeSingleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingletonInjectionTest extends AbstractInjectionTest {

	@Test
	public void testSequenceSingletonInjection() {
		InjectableWithSequenceSingletonInjectables injectable = this.suite
				.injectInSuiteContext(InjectableWithSequenceSingletonInjectables.class);

		Assertions.assertSame(injectable.a.sequenceSingleton, injectable.b.sequenceSingleton);

		Injectable singleton = new Injectable();
		InjectableWithInjector main = this.suite.injectInSuiteContext(InjectableWithInjector.class,
				SingletonAllocation.of(InjectableWithSequenceSingleton.SINGLETON, singleton));
		InjectableWithSequenceSingleton childA = main.injector.instantiate(InjectableWithSequenceSingleton.class);
		InjectableWithSequenceSingleton childB = main.injector.instantiate(InjectableWithSequenceSingleton.class);

		Assertions.assertSame(singleton, childA.sequenceSingleton);
		Assertions.assertSame(singleton, childB.sequenceSingleton);
	}
	
	@Test
	public void testSingletonInjectionWithoutInject() {
		Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithSingletonWithoutInject.class));
	}

	@Test
	public void testWrongTypeSingletonInjection() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInSuiteContext(UninjectableWithWrongTypeSingleton.class));
	}

	@Test
	public void testExplicitSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitSingleton.class);

		Assertions.assertTrue(injectable.explicitInjectable == null);
	}

	@Test
	public void testBothExplicitAndEagerSingletonInjection() {
		InjectableWithExplicitAndEagerSingleton injectable = this.suite
				.injectInSuiteContext(InjectableWithExplicitAndEagerSingleton.class);

		Assertions.assertTrue(injectable.eagerInjectable != null);
		Assertions.assertTrue(injectable.explicitInjectable == null);
	}

	@Test
	public void testDifferentTypeSingletonInjectionWithAllocation() {
		Injectable bean = new Injectable();
		SingletonAllocation singleton = Blueprint.SingletonAllocation.of(InjectableWithSingletonAllocationRequired.SINGLETON, bean);

		InjectableWithSingletonAllocationRequired injectable = this.suite
				.injectInSuiteContext(InjectableWithSingletonAllocationRequired.class, singleton);

		Assertions.assertSame(bean, injectable.implSingleton);
		Assertions.assertSame(bean, injectable.interfaceSingleton);
	}

	@Test
	public void testDifferentTypeSingletonInjectionWithoutAllocation() {
		Assertions.assertThrows(InjectionException.class, () -> this.suite.injectInRootContext(InjectableWithSingletonAllocationRequired.class));
	}

	@Test
	public void testSingletonMapping() {
		String qualifier = "theQualifierToMapTo";
		Injectable singleton = new Injectable();
		Injector rootInjector = Injector.of(SingletonAllocation.of(qualifier, singleton));

		InjectableWithSequenceSingleton injectable = rootInjector.instantiate(InjectableWithSequenceSingleton.class,
				Blueprint.MappingAllocation.of(InjectableWithSequenceSingleton.SINGLETON, qualifier));

		Assertions.assertSame(singleton, injectable.sequenceSingleton);
	}

	@Test
	public void testDefineDoubleMapping() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> Injector.of(
				Blueprint.MappingAllocation.of("1", "2"),
				Blueprint.MappingAllocation.of("1", "3")));
	}

	@Test
	public void testDefineLoopedMappings() {
		Assertions.assertThrows(MappingException.class, () -> Injector.of(
				Blueprint.MappingAllocation.of("1", "2"),
				Blueprint.MappingAllocation.of("2", "3"),
				Blueprint.MappingAllocation.of("3", "1")));
	}
}
