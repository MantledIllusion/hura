package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.Injectable;

public class InjectableWithManualInjectionOnRootInjectorDuringInjectPhase {

	@PostInject
	private void init() {
		Injector.of().instantiate(Injectable.class);
	}
}
