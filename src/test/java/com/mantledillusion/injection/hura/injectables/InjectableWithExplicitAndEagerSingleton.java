package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Optional;

public class InjectableWithExplicitAndEagerSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	public Injectable eagerInjectable;
	
	@Inject(SINGLETON)
	@Optional
	public Injectable explicitInjectable;
}
