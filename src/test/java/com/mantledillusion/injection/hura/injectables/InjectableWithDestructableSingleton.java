package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithDestructableSingleton {

	public static final String SINGLETON_ID = "singletonId";
	
	@Inject(SINGLETON_ID)
	public InjectableWithDestructionAwareness singleton;
}
