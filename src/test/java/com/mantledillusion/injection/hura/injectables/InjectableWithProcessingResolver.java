package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Process;

public class InjectableWithProcessingResolver {

	public String propertyValue;
	
	@Process(Phase.INSPECT)
	private void postProcess(TemporalInjectorCallback callback) {
		this.propertyValue = callback.resolve("property.key");
	}
}
