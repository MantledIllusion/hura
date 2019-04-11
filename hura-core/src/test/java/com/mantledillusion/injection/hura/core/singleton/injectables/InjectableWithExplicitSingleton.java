package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;

public class InjectableWithExplicitSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(SINGLETON)
	@Optional
	public InjectableInterface explicitInjectable;
}
