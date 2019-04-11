package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class InjectableWithSingletonAllocationRequired {
	
	public static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public InjectableInterface interfaceSingleton;

	@Inject
	@Qualifier(SINGLETON)
	public Injectable implSingleton;
}
