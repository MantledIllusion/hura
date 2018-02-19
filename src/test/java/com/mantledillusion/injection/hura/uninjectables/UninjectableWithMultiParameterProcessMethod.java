package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.Process;

public class UninjectableWithMultiParameterProcessMethod {

	@Process
	private void postProcess(TemporalInjectorCallback callback1, TemporalInjectorCallback callback2) {
		
	}
}
