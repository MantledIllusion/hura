package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class InjectableWithSingletonAllocationRequiredAndInjector {
	
	public static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public InjectableInterface interfaceSingleton;

	@Inject
	public Injector injector;
}
