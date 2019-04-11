package com.mantledillusion.injection.hura.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithManualInjectionOnInjectedInjectorDuringInjectPhase {
	
	@Inject
	private Injector injector;

	@PostInject
	private void init() {
		this.injector.instantiate(Injectable.class);
	}
}
