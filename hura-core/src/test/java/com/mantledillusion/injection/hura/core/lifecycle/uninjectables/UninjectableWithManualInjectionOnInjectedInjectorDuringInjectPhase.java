package com.mantledillusion.injection.hura.core.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;

public class UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase {
	
	@Inject
	private Injector injector;

	@PostInject
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
