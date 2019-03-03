package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class UninjectableWithGlobalSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject(value=SINGLETON)
	@Global
	public Injector injector;
}
