package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

public class UninjectableWithGlobalSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject(value=SINGLETON, singletonMode=SingletonMode.GLOBAL)
	public Injector injector;
}
