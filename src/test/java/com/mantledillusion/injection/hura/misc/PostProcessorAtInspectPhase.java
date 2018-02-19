package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessor;

public class PostProcessorAtInspectPhase implements Processor<InjectableWithProcessor> {

	@Override
	public void process(InjectableWithProcessor bean, TemporalInjectorCallback callback) throws Exception {
		bean.injectableAtInspect= bean.injectable;
		bean.occurredPhases.add(Phase.INSPECT);
	}
}
