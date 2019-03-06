package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class InjectableWithSingletonAllocationRequired {
	
	public static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public InjectableInterface interfaceSingleton;

	@Inject
	@Qualifier(SINGLETON)
	public Injectable implSingleton;
}
