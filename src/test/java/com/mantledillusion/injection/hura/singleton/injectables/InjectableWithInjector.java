package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class InjectableWithInjector {

	@Inject
	public Injector injector;
}
