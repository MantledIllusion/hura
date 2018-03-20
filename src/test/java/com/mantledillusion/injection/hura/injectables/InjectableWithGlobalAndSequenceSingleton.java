package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

public class InjectableWithGlobalAndSequenceSingleton {
	
	@Inject(value="globalSingleton", singletonMode=SingletonMode.GLOBAL)
	public InjectableWithSequenceSingleton globalSingleton;
	
	@Inject(value=InjectableWithSequenceSingleton.SINGLETON)
	public Injectable sequenceSingleton;
}
