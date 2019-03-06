package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;

public class InjectableWithGlobalSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(value = SINGLETON, mode = SingletonMode.GLOBAL)
    public Injectable globalSingleton;
}
