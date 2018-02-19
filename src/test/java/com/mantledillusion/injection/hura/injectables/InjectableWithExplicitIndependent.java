package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.InjectionMode;
import com.mantledillusion.injection.hura.misc.InjectableInterface;

public class InjectableWithExplicitIndependent {
	
	@Inject(injectionMode=InjectionMode.EXPLICIT)
	public InjectableInterface explicitInjectable;
}
