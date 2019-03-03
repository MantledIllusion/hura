package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.InjectableInterface;

public class InjectableWithSingletonAllocationRequired {
	
	public static final String SINGLETON = "singleton";

	@Inject(SINGLETON)
	public InjectableInterface interfaceSingleton;

	@Inject(SINGLETON)
	public Injectable implSingleton;
}
