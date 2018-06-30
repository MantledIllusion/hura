package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithGlobalAndSequenceSingleton {
	
	@Inject("globalSingleton")
	@Global
	public InjectableWithSequenceSingleton globalSingleton;
	
	@Inject(value=InjectableWithSequenceSingleton.SINGLETON)
	public Injectable sequenceSingleton;
}
