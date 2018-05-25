package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Process;

public class InjectableWithManualInjectionOnRootInjectorDuringInjectPhase {
	
	@Process(Phase.INJECT)
	private void init() {
		Injector.of().instantiate(Injectable.class);
	}
}
