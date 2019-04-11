package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;

public class InjectableWithManualInjectionOnInjectedInjectorDuringFinalizePhase {
	
	@Inject
	private Injector injector;

	@PostConstruct
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
