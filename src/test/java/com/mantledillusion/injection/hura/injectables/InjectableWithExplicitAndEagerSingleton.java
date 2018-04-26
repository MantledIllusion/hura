package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.InjectionMode;

public class InjectableWithExplicitAndEagerSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON)
	public Injectable eagerInjectable;
	
	@Inject(value=SINGLETON, injectionMode=InjectionMode.EXPLICIT)
	public Injectable explicitInjectable;
}
