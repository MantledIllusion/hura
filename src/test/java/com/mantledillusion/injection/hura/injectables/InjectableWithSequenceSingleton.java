package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithSequenceSingleton {
	
	private static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON)
	public Injectable sequenceSingleton;
	
	@Inject
	public Injector subInjector;
}
