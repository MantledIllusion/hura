package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class UninjectableWithSequenceSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public Injector injector;
}
