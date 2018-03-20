package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithSequenceSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON)
	public Injectable sequenceSingleton;
}
