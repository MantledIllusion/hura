package com.mantledillusion.injection.hura.core.lifecycle;

import com.mantledillusion.injection.hura.core.*;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.exception.ShutdownException;
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
    public void testInjectParameterMethodInjectionDuringPostInject() {
        InjectableWithInjectedParameterMethodDuringPostInjectPhase injectable = this.suite.injectInSuiteContext(InjectableWithInjectedParameterMethodDuringPostInjectPhase.class);
        Assertions.assertSame(injectable.sequenceSingleton, injectable.methodInjectedBean.sequenceSingleton);
    }

    @Test
    public void testInjectParameterMethodInjectionDuringPostConstruct() {
        Assertions.assertThrows(ProcessorException.class, () -> suite.injectInSuiteContext(InjectableWithInjectedParameterMethodDuringPostConstructPhase.class));
    }

    @Test
    public void testPluginParameterMethodInjectionDuringPostInject() {
        InjectableWithPluginParameterMethodDuringPostInjectPhase injectable = this.suite.injectInSuiteContext(InjectableWithPluginParameterMethodDuringPostInjectPhase.class);
        Assertions.assertNotNull(injectable.methodInjectedBean);
        Assertions.assertNotNull(injectable.methodInjectedBean.getBean());
    }

    @Test
    public void testPluginParameterMethodInjectionDuringPostConstruct() {
        Assertions.assertThrows(ProcessorException.class, () -> suite.injectInSuiteContext(InjectableWithPluginParameterMethodDuringPostConstructPhase.class));
    }

    @Test
    public void testResolvedParameterMethodInjectionDuringPostInject() {
        String propertyValue = "value";
        InjectableWithResolvedParameterDuringPostInjectPhase injectable = this.suite.injectInSuiteContext(InjectableWithResolvedParameterDuringPostInjectPhase.class,
                Blueprint.PropertyAllocation.of(InjectableWithResolvedParameterDuringPostInjectPhase.PROPERTY_KEY, propertyValue));
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertEquals(propertyValue, injectable.methodResolvedValue);
    }

    @Test
    public void testResolvedParameterMethodInjectionDuringPostDestroy() {
        Assertions.assertThrows(ProcessorException.class, () -> suite.injectInSuiteContext(InjectableWithResolvedParameterDuringPostDestroyPhase.class));
    }

    @Test
    public void testAggregatedParameterMethodInjectionDuringPostConstruct() {
        InjectableWithAggregatedParameterMethodDuringPostConstructPhase injectable = this.suite.injectInSuiteContext(InjectableWithAggregatedParameterMethodDuringPostConstructPhase.class,
                Blueprint.SingletonAllocation.allocateToInstance("q0", new Injectable()),
                Blueprint.SingletonAllocation.allocateToInstance("q1", new Injectable()),
                Blueprint.SingletonAllocation.allocateToInstance("q2", new Injectable()));
        Assertions.assertNotNull(injectable.aggregatedByMethod);
        Assertions.assertEquals(2, injectable.aggregatedByMethod.size());
        Assertions.assertEquals(injectable.aggregatedByField, injectable.aggregatedByMethod);
    }

    @Test
    public void testAggregatedParameterMethodInectionDuringPostInject() {
        Assertions.assertThrows(ProcessorException.class, () -> suite.injectInSuiteContext(InjectableWithAggregatedParameterMethodDuringPostInjectPhase.class));
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
    public void testInjectorLifecycle() {
        BeanProcessor<InjectableWithInjector> processor = (phase, bean, callback) ->
                Assertions.assertFalse(bean.injector.isActive());

        InjectableWithInjector injectable = this.suite.injectInSuiteContext(InjectableWithInjector.class,
                Blueprint.TypeAllocation.allocateToType(InjectableWithInjector.class, InjectableWithInjector.class,
                        PhasedBeanProcessor.of(processor, Phase.POST_INJECT)));

        Assertions.assertTrue(injectable.injector.isActive());

        InjectableWithDestructionAwareness sub = injectable.injector.instantiate(InjectableWithDestructionAwareness.class);

        Assertions.assertFalse(sub.wasDestructed);
        this.suite.destroyInSuiteContext(injectable);
        Assertions.assertTrue(sub.wasDestructed);

        Assertions.assertFalse(injectable.injector.isActive());

        Assertions.assertThrows(ShutdownException.class, () -> injectable.injector.destroy(sub));
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
        Injector.RootInjector rootInjector = Injector.of(Blueprint.SingletonAllocation.allocateToType(
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
