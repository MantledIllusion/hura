package com.mantledillusion.injection.hura.singleton.injectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class InjectableWithSequenceSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(value=SINGLETON)
	public Injectable sequenceSingleton;
}
