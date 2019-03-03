package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;

public class InjectableWithExplicitAndEagerSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	public Injectable eagerInjectable;
	
	@Inject(SINGLETON)
	@Optional
	public Injectable explicitInjectable;
}
