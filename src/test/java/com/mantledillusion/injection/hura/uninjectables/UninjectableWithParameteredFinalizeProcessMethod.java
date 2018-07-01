package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Process;

public class UninjectableWithParameteredFinalizeProcessMethod {

	@Process(Phase.FINALIZE)
	public void finalize(TemporalInjectorCallback injectable) {
		
	}
}
