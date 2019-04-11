package com.mantledillusion.injection.hura.core.context.uninjectables;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class UninjectableWithSequenceSingletonInjector {
	
	private static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public Injector injector;
}
