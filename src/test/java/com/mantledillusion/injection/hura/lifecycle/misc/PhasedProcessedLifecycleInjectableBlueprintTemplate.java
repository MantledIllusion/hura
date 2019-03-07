package com.mantledillusion.injection.hura.lifecycle.misc;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.BeanProcessor;
import com.mantledillusion.injection.hura.lifecycle.injectables.AbstractLifecycleInjectable;
import com.mantledillusion.injection.hura.lifecycle.injectables.PhasedProcessedLifecycleInjectable;

public class PhasedProcessedLifecycleInjectableBlueprintTemplate implements Blueprint.TypedBlueprintTemplate<PhasedProcessedLifecycleInjectable> {

    private class PhasedProcessedLifecycleInjectableProcessor implements BeanProcessor<PhasedProcessedLifecycleInjectable> {

        @Override
        public void process(Phase phase, PhasedProcessedLifecycleInjectable bean, Injector.TemporalInjectorCallback callback) throws Exception {
            AbstractLifecycleInjectable.add(phase, bean, callback);
        }
    }

    @Override
    public Class<PhasedProcessedLifecycleInjectable> getRootType() {
        return PhasedProcessedLifecycleInjectable.class;
    }

    @Define
    public Predefinable.Property defineProperty() {
        return Predefinable.Property.of(AbstractLifecycleInjectable.IMPL_PROPERTY_KEY, PhasedProcessedLifecycleInjectable.class.getName());
    }

    @Define
    public BeanAllocation<PhasedProcessedLifecycleInjectable> allocate() {
        PhasedProcessedLifecycleInjectableProcessor processor = new PhasedProcessedLifecycleInjectableProcessor();
        return BeanAllocation.allocateToType(PhasedProcessedLifecycleInjectable.class,
                PhasedBeanProcessor.of(processor, Phase.PRE_CONSTRUCT),
                PhasedBeanProcessor.of(processor, Phase.POST_INJECT),
                PhasedBeanProcessor.of(processor, Phase.POST_CONSTRUCT),
                PhasedBeanProcessor.of(processor, Phase.PRE_DESTROY),
                PhasedBeanProcessor.of(processor, Phase.POST_DESTROY));
    }
}
