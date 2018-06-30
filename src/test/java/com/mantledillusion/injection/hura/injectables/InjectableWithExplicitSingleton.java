package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.misc.InjectableInterface;

public class InjectableWithExplicitSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	@Optional
	public InjectableInterface explicitInjectable;
}
