package com.mantledillusion.injection.hura.core.lifecycle;

import com.mantledillusion.injection.hura.core.*;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.lifecycle.injectables.*;
import com.mantledillusion.injection.hura.core.lifecycle.misc.PhasedProcessedLifecycleInjectableBlueprint;
import com.mantledillusion.injection.hura.core.lifecycle.uninjectables.UninjectableWithFailingProcessor;
import com.mantledillusion.injection.hura.core.lifecycle.uninjectables.UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase;
import com.mantledillusion.injection.hura.core.lifecycle.uninjectables.UninjectableWithStaticProcessMethod;
import com.mantledillusion.injection.hura.core.lifecycle.uninjectables.UninjectableWithWrongTypeParameterProcessMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class LifecycleTest extends AbstractInjectionTest {

    private static final List<Phase> PHASES_WITH_PRECONSTRUCT = Arrays.asList(Phase.PRE_CONSTRUCT, Phase.POST_INJECT, Phase.POST_CONSTRUCT, Phase.PRE_DESTROY, Phase.POST_DESTROY);
    private static final List<Phase> PHASES_WITHOUT_PRECONSTRUCT = Arrays.asList(Phase.POST_INJECT, Phase.POST_CONSTRUCT, Phase.PRE_DESTROY, Phase.POST_DESTROY);

    @Test
    public void testAnnotatedClassProcessing() {
        ClassProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(ClassProcessedLifecycleInjectable.class,
                Blueprint.PropertyAllocation.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, ClassProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(ClassProcessedLifecycleInjectable.class));
    }

    @Test
    public void testAnnotatedMethodProcessing() {
        MethodProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(MethodProcessedLifecycleInjectable.class,
                Blueprint.PropertyAllocation.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, MethodProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertEquals(PHASES_WITHOUT_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(MethodProcessedLifecycleInjectable.class));
    }

    @Test
    public void testAnnotatedAnnotationProcessing() {
        AnnotationProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(AnnotationProcessedLifecycleInjectable.class,
                Blueprint.PropertyAllocation.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, AnnotationProcessedLifecycleInjectable.class.getName()));
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(AnnotationProcessedLifecycleInjectable.class));
    }

    @Test
    public void testBlueprintPhasedProcessing() {
        PhasedProcessedLifecycleInjectable injectable = this.suite.injectInSuiteContext(PhasedProcessedLifecycleInjectable.class,
                new PhasedProcessedLifecycleInjectableBlueprint());
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertEquals(PHASES_WITH_PRECONSTRUCT, AbstractLifecycleInjectable.PHASES.get(PhasedProcessedLifecycleInjectable.class));
    }

    @Test
    public void testStaticPostProcessMethodInjection() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithStaticProcessMethod.class));
    }

    @Test
    public void testWrongParameterTypePostProcessMethodInjection() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithWrongTypeParameterProcessMethod.class));
    }

    @Test
    public void testBlueprintPostProcessingWithoutPostProcessor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhasedBeanProcessor.of(null, Phase.POST_CONSTRUCT));
    }

    @Test
    public void testBlueprintPostProcessingWithoutPhase() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> PhasedBeanProcessor.of(((phase, bean, callback) -> {}), null));
    }

    @Test
    public void testFailingPostProcessing() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithFailingProcessor.class));
    }

    @Test
    public void testUnknownInstanceDestruction() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.suite.destroyInSuiteContext(new Object()));
    }

    @Test
    public void testInjectorDestruction() {
        InjectableWithInjector injectable = this.suite.injectInSuiteContext(InjectableWithInjector.class);
        InjectableWithDestructionAwareness sub = injectable.injector.instantiate(InjectableWithDestructionAwareness.class);

        Assertions.assertFalse(sub.wasDestructed);
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertTrue(sub.wasDestructed);
    }

    @Test
    public void testRootInjectorDestruction() {
        Injector.RootInjector rootInjector = Injector.of();
        InjectableWithDestructionAwareness injectable = rootInjector.instantiate(InjectableWithDestructionAwareness.class);

        Assertions.assertFalse(injectable.wasDestructed);
        rootInjector.shutdown();
        Assertions.assertTrue(injectable.wasDestructed);
    }

    @Test
    public void testSingletonDestruction() {
        InjectableWithDestructionAwareness.InjectableWithDestructableSingletonAndInjector injectable =
                this.suite.injectInSuiteContext(InjectableWithDestructionAwareness.InjectableWithDestructableSingletonAndInjector.class);
        InjectableWithDestructionAwareness.InjectableWithDestructableSingleton sub =
                injectable.injector.instantiate(InjectableWithDestructionAwareness.InjectableWithDestructableSingleton.class);

        Assertions.assertFalse(injectable.singleton.wasDestructed);
        injectable.injector.destroy(sub);
        Assertions.assertFalse(injectable.singleton.wasDestructed);
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertTrue(injectable.singleton.wasDestructed);
    }

    @Test
    public void testRootSingletonDestruction() {
        Injector.RootInjector rootInjector = Injector.of(Blueprint.SingletonAllocation.of(
                InjectableWithDestructionAwareness.InjectableWithDestructableSingleton.QUALIFIER,
                InjectableWithDestructionAwareness.class));

        InjectableWithDestructionAwareness.InjectableWithDestructableSingleton injectable =
                rootInjector.instantiate(InjectableWithDestructionAwareness.InjectableWithDestructableSingleton.class);

        Assertions.assertFalse(injectable.singleton.wasDestructed);
        rootInjector.destroy(injectable);
        Assertions.assertFalse(injectable.singleton.wasDestructed);
        rootInjector.shutdown();
        Assertions.assertTrue(injectable.singleton.wasDestructed);
    }

    @Test
    public void testManualInjectionOnInjectedInjectorDuringInjectProcessing() {
        Assertions.assertThrows(ProcessorException.class, () -> this.suite.injectInSuiteContext(UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase.class));
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
