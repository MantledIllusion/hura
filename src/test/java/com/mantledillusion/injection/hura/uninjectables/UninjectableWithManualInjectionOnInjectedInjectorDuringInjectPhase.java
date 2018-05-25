package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase {
	
	@Inject
	private Injector injector;
	
	@Process(Phase.INJECT)
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
