package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct;
import com.mantledillusion.injection.hura.Injectable;

public class InjectableWithManualInjectionOnInjectedInjectorDuringFinalizePhase {
	
	@Inject
	private Injector injector;

	@PostConstruct
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
