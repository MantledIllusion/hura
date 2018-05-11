package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithDestructableSingleton {

	public static final String QUALIFIER = "qualifier";
	
	@Inject(QUALIFIER)
	public InjectableWithDestructionAwareness singleton;
}
