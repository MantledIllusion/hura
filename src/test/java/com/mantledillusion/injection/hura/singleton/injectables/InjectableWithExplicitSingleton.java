package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.InjectableInterface;

public class InjectableWithExplicitSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	@Optional
	public InjectableInterface explicitInjectable;
}
