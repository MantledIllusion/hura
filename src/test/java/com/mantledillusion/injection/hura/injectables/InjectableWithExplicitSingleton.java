package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.InjectionMode;
import com.mantledillusion.injection.hura.misc.InjectableInterface;

public class InjectableWithExplicitSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON, injectionMode=InjectionMode.EXPLICIT)
	public InjectableInterface explicitInjectable;
}
