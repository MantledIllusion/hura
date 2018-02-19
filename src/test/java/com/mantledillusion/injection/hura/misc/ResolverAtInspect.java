package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessorResolver;

public class ResolverAtInspect implements Processor<InjectableWithProcessorResolver> {

	@Override
	public void process(InjectableWithProcessorResolver bean, TemporalInjectorCallback callback) throws Exception {
		bean.propertyValue = callback.resolve("property.key");
	}
}
