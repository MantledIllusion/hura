package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWithGlobalSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject(value=SINGLETON)
	@Global
	public Injector injector;
}
