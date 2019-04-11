package com.mantledillusion.injection.hura.core.aggregation;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.InjectableInterfaceImpl;
import com.mantledillusion.injection.hura.core.aggregation.injectables.*;
import com.mantledillusion.injection.hura.core.aggregation.misc.PropertyDependentPredicate;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationAggregationTest extends AbstractInjectionTest {

    @Test
    public void testUnfilteredCollectionAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        InjectableWithUnfilteredCollectionAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredCollectionAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(2, injectable.singletons.size());
        Assert.assertTrue(injectable.singletons.contains(singletonA));
        Assert.assertTrue(injectable.singletons.contains(singletonB));
    }

    @Test
    public void testUnfilteredListAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        InjectableWithUnfilteredListAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredListAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(2, injectable.singletons.size());
        Assert.assertTrue(injectable.singletons.contains(singletonA));
        Assert.assertTrue(injectable.singletons.contains(singletonB));
    }

    @Test
    public void testUnfilteredSetAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        InjectableWithUnfilteredSetAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredSetAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(2, injectable.singletons.size());
        Assert.assertTrue(injectable.singletons.contains(singletonA));
        Assert.assertTrue(injectable.singletons.contains(singletonB));
    }

    @Test
    public void testUnfilteredSingleAggregation() {
        Injectable singleton = new Injectable();

        InjectableWithUnfilteredSingleAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredSingleAggregation.class,
                Blueprint.SingletonAllocation.of("singleton", singleton));

        Assert.assertNotNull(injectable.singleton);
        Assert.assertSame(singleton, injectable.singleton);
    }

    @Test(expected = ProcessorException.class)
    public void testUnfilteredNonOptionalSingleAggregation() {
        this.suite.injectInRootContext(InjectableWithUnfilteredSingleAggregation.class);
    }

    @Test
    public void testUnfilteredOptionalSingleAggregation() {
        InjectableWithUnfilteredOptionalSingleAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredOptionalSingleAggregation.class);

        Assert.assertNull(injectable.singleton);
    }

    @Test(expected = ProcessorException.class)
    public void testUnfilteredNonDistinctSingleAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        this.suite.injectInRootContext(InjectableWithUnfilteredSingleAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));
    }

    @Test
    public void testUnfilteredDistinctSingleAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();

        InjectableWithUnfilteredDistinctSingleAggregation injectable = this.suite.injectInRootContext(InjectableWithUnfilteredDistinctSingleAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Assert.assertTrue(injectable.singleton == singletonA || injectable.singleton == singletonB);
    }

    @Test
    public void testTypeFilteredAggregation() {
        Injectable singletonA = new Injectable();
        InjectableInterfaceImpl singletonB = new InjectableInterfaceImpl();

        InjectableWithTypeFilteredAggregation injectable = this.suite.injectInRootContext(InjectableWithTypeFilteredAggregation.class,
                Blueprint.SingletonAllocation.of("singletonA", singletonA),
                Blueprint.SingletonAllocation.of("singletonB", singletonB));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(1, injectable.singletons.size());
        Assert.assertSame(singletonB, injectable.singletons.stream().findFirst().get());
    }

    @Test
    public void testQualifierFilteredAggregation() {
        Injectable singletonA = new Injectable();
        Injectable singletonB = new Injectable();
        Injectable singletonC = new Injectable();

        InjectableWithQualifierFilteredAggregation injectable = this.suite.injectInRootContext(InjectableWithQualifierFilteredAggregation.class,
                Blueprint.SingletonAllocation.of(InjectableWithQualifierFilteredAggregation.QUALIFIER_PREFIX+"A", singletonA),
                Blueprint.SingletonAllocation.of(InjectableWithQualifierFilteredAggregation.QUALIFIER_PREFIX+"B", singletonB),
                Blueprint.SingletonAllocation.of("someSingletonC", singletonC));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(2, injectable.singletons.size());
        Assert.assertTrue(injectable.singletons.contains(singletonA));
        Assert.assertTrue(injectable.singletons.contains(singletonB));
    }

    @Test
    public void testPredicateFilteredAggregation() {
        InjectableWithName singletonA = new InjectableWithName("singletonA");
        InjectableWithName singletonB = new InjectableWithName("singletonB");

        InjectableWithPredicateFilteredCollectionAggregation injectable = this.suite.injectInRootContext(InjectableWithPredicateFilteredCollectionAggregation.class,
                Blueprint.SingletonAllocation.of("A", singletonA),
                Blueprint.SingletonAllocation.of("B", singletonB),
                Blueprint.PropertyAllocation.of(PropertyDependentPredicate.NAME_PROPERTY_ID, "singletonB"));

        Assert.assertNotNull(injectable.singletons);
        Assert.assertEquals(1, injectable.singletons.size());
        Assert.assertTrue(injectable.singletons.contains(singletonB));
    }
}
