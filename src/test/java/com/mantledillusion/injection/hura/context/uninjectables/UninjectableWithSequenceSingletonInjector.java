package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class UninjectableWithSequenceSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject(value=SINGLETON)
	public Injector injector;
}
