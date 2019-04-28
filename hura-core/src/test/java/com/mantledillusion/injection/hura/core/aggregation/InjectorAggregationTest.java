package com.mantledillusion.injection.hura.core.aggregation;

import com.mantledillusion.injection.hura.core.*;
import com.mantledillusion.injection.hura.core.aggregation.injectables.InjectableWithInjectorAndSingleton;
import com.mantledillusion.injection.hura.core.aggregation.injectables.InjectableWithName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class InjectorAggregationTest extends AbstractInjectionTest {

    @Test
    public void testStaticAllocatedSingletonAggregation() {
        Injectable singleton = new Injectable();

        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.SingletonAllocation.of(InjectableWithInjectorAndSingleton.QUALIFIER, singleton));

        Collection<Object> allSingletons = injectable.injector.aggregate();
        Assertions.assertEquals(1, allSingletons.size());
        Assertions.assertSame(singleton, allSingletons.stream().findFirst().get());
    }

    @Test
    public void testDynamicAllocatedSingletonAggregation() {
        InjectableWithInjectorAndSingleton injectable = this.suite.injectInRootContext(InjectableWithInjectorAndSingleton.class);

        Collection<Object> allSingletons = injectable.injector.aggregate();
        Assertions.assertEquals(1, allSingletons.size());
        Assertions.assertSame(injectable.singleton, allSingletons.stream().findFirst().get());
    }

    @Test
    public void testAggregationTypeFiltering() {
        Injectable singletonA = new Injectable();
        InjectableInterfaceImpl singletonB = new InjectableInterfaceImpl();

        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Collection<InjectableInterfaceImpl> allSingletons = injectable.injector.aggregate(InjectableInterfaceImpl.class);
        Assertions.assertEquals(1, allSingletons.size());
        Assertions.assertSame(singletonB, allSingletons.stream().findFirst().get());
    }

    @Test
    public void testAggregationQualifierFiltering() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Collection<Object> allSingletons = injectable.injector.aggregate(".*B");
        Assertions.assertEquals(1, allSingletons.size());
        Assertions.assertSame(singletonB, allSingletons.stream().findFirst().get());
    }

    @Test
    public void testAggregationPredicateFiltering() {
        InjectableWithName singletonA = new InjectableWithName("singletonA");
        InjectableWithName singletonB = new InjectableWithName("singletonB");

        InjectableWithInjector injectable = this.suite.injectInRootContext(InjectableWithInjector.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Collection<InjectableWithName> allSingletons = injectable.injector.aggregate(InjectableWithName.class, (qualifier, bean) -> bean.name.equals("singletonB"));
        Assertions.assertEquals(1, allSingletons.size());
        Assertions.assertSame(singletonB, allSingletons.stream().findFirst().get());
    }
}
