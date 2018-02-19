package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessor;

public class PostProcessorAtInjectPhase implements Processor<InjectableWithProcessor> {

	@Override
	public void process(InjectableWithProcessor bean, TemporalInjectorCallback callback) throws Exception {
		bean.injectableAtInject = bean.injectable;
		bean.occurredPhases.add(Phase.INJECT);
	}
}
