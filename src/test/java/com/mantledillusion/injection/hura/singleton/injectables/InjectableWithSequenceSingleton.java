package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class InjectableWithSequenceSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject(value=SINGLETON)
	public Injectable sequenceSingleton;
}
