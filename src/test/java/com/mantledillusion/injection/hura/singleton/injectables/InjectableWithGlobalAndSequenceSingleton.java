package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;

public class InjectableWithGlobalAndSequenceSingleton {
	
	@Inject
	@Qualifier(value = "globalSingleton", mode = SingletonMode.GLOBAL)
    public InjectableWithSequenceSingleton globalSingleton;
	
	@Inject
	@Qualifier(InjectableWithSequenceSingleton.SINGLETON)
	public Injectable sequenceSingleton;
}
