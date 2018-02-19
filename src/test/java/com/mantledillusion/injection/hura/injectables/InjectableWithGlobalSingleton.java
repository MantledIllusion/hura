package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

public class InjectableWithGlobalSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON, singletonMode=SingletonMode.GLOBAL)
	public Injectable globalSingleton;
}
