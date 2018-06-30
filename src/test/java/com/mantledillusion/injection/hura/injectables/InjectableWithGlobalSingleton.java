package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithGlobalSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	@Global
	public Injectable globalSingleton;
}
