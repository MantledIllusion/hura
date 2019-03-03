package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSequenceSingleton;

public class InjectableWithGlobalAndSequenceSingleton {
	
	@Inject("globalSingleton")
	@Global
	public InjectableWithSequenceSingleton globalSingleton;
	
	@Inject(value=InjectableWithSequenceSingleton.SINGLETON)
	public Injectable sequenceSingleton;
}
