package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class InjectableWithGlobalSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	@Global
	public Injectable globalSingleton;
}
