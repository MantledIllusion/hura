package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithDestructableSingletonAndInjector {

	public static final String SINGLETON_ID = "singletonId";
	
	@Inject(SINGLETON_ID)
	public InjectableWithDestructionAwareness singleton;
	@Inject
	public Injector injector;
}
