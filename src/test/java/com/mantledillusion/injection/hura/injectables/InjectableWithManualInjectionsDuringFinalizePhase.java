package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Process;

public class InjectableWithManualInjectionsDuringFinalizePhase {
	
	@Inject
	private Injector injector;
	
	@Process(Phase.FINALIZE)
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
