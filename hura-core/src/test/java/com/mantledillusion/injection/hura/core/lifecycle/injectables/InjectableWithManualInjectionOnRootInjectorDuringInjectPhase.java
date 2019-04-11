package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;

public class InjectableWithManualInjectionOnRootInjectorDuringInjectPhase {

	@PostInject
	private void init() {
		Injector.of().instantiate(Injectable.class);
	}
}
