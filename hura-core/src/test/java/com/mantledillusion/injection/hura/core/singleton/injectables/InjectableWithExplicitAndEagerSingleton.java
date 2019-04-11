package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;

public class InjectableWithExplicitAndEagerSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(SINGLETON)
	public Injectable eagerInjectable;
	
	@Inject
	@Qualifier(SINGLETON)
	@Optional
	public Injectable explicitInjectable;
}
