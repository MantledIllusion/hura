package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.InjectableInterface;

public class InjectableWithSingletonAllocationRequired {
	
	public static final String SINGLETON = "singleton";

	@Inject(SINGLETON)
	public InjectableInterface interfaceSingleton;

	@Inject(SINGLETON)
	public Injectable implSingleton;
}
