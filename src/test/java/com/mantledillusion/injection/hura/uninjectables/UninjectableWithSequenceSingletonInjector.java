package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWithSequenceSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject(value=SINGLETON)
	public Injector injector;
}
