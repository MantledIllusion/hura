package com.mantledillusion.injection.hura.lifecycle;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import com.mantledillusion.injection.hura.lifecycle.injectables.*;
import com.mantledillusion.injection.hura.lifecycle.misc.PhasedProcessedLifecycleInjectableBlueprintTemplate;
import com.mantledillusion.injection.hura.lifecycle.uninjectables.UninjectableWithFailingProcessor;
import com.mantledillusion.injection.hura.lifecycle.uninjectables.UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase;
import com.mantledillusion.injection.hura.lifecycle.uninjectables.UninjectableWithStaticProcessMethod;
import com.mantledillusion.injection.hura.lifecycle.uninjectables.UninjectableWithWrongTypeParameterProcessMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LifecycleTest extends AbstractInjectionTest {

    private static final List<Phase> PHASES_WITH_PRECONSTRUCT = Arrays.asList(Phase.PRE_CONSTRUCT, Phase.POST_INJECT, Phase.POST_CONSTRUCT, Phase.PRE_DESTROY, Phase.POST_DESTROY);
    private static final List<Phase> PHASES_WITHOUT_PRECONSTRUCT = Arrays.asList(Phase.POST_INJECT, Phase.POST_CONSTRUCT, Phase.PRE_DESTROY, Phase.POST_DESTROY);

    @Test
    public void testAnnotatedClassProcessing() {
        ClassProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(ClassProcessedLifecycleInjectable.class,
                Predefinable.Property.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, ClassProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assert.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(ClassProcessedLifecycleInjectable.class));
    }

    @Test
    public void testAnnotatedMethodProcessing() {
        MethodProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(MethodProcessedLifecycleInjectable.class,
                Predefinable.Property.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, MethodProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assert.assertEquals(PHASES_WITHOUT_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(MethodProcessedLifecycleInjectable.class));
    }

    @Test
    public void testAnnotatedAnnotationProcessing() {
        AnnotationProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(AnnotationProcessedLifecycleInjectable.class,
                Predefinable.Property.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, AnnotationProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assert.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(AnnotationProcessedLifecycleInjectable.class));
    }

    @Test
    public void testBlueprintPhasedProcessing() {
        PhasedProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(Blueprint.from(new PhasedProcessedLifecycleInjectableBlueprintTemplate()));
        this.suite.destroyInSuiteContext(injectable);
        Assert.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(PhasedProcessedLifecycleInjectable.class));
    }

    @Test(expected=ProcessorException.class)
    public void testStaticPostProcessMethodInjection() {
        this.suite.injectInSuiteContext(UninjectableWithStaticProcessMethod.class);
    }

    @Test(expected=ProcessorException.class)
    public void testWrongParameterTypePostProcessMethodInjection() {
        this.suite.injectInSuiteContext(UninjectableWithWrongTypeParameterProcessMethod.class);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testBlueprintPostProcessingWithoutPostProcessor() {
        PhasedBeanProcessor.of(null, Phase.POST_CONSTRUCT);
    }

    @Test(expected= IllegalArgumentException.class)
    public void testBlueprintPostProcessingWithoutPhase() {
        PhasedBeanProcessor.of(((phase, bean, callback) -> {}), null);
    }

    @Test(expected=ProcessorException.class)
    public void testFailingPostProcessing() {
        this.suite.injectInSuiteContext(UninjectableWithFailingProcessor.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testUnknownInstanceDestruction() {
        this.suite.destroyInSuiteContext(new Object());
    }

    @Test
    public void testSingletonDestruction() {
        Injector.RootInjector rootInjector = Injector.of();

        InjectableWithDestructionAwareness.InjectableWithDestructableSingletonAndInjector injectable =
                rootInjector.instantiate(InjectableWithDestructionAwareness.InjectableWithDestructableSingletonAndInjector.class);
        InjectableWithDestructionAwareness.InjectableWithDestructableSingleton sub =
                injectable.injector.instantiate(InjectableWithDestructionAwareness.InjectableWithDestructableSingleton.class);

        Assert.assertFalse(injectable.singleton.wasDestructed);
        injectable.injector.destroy(sub);
        Assert.assertFalse(injectable.singleton.wasDestructed);
        rootInjector.destroy(injectable);
        Assert.assertTrue(injectable.singleton.wasDestructed);
    }

    @Test(expected=ProcessorException.class)
    public void testManualInjectionOnInjectedInjectorDuringInjectProcessing() {
        this.suite.injectInSuiteContext(UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase.class);
    }

    @Test
    public void testManualInjectionOnInjectedInjectorDuringFinalizeProcessing() {
        this.suite.injectInSuiteContext(InjectableWithManualInjectionOnInjectedInjectorDuringFinalizePhase.class);
    }

    @Test
    public void testManualInjectionOnRootInjectorDuringInjectProcessing() {
        this.suite.injectInSuiteContext(InjectableWithManualInjectionOnRootInjectorDuringInjectPhase.class);
    }
}
