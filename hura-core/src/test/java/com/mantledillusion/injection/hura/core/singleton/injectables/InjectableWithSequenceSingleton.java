package com.mantledillusion.injection.hura.core.singleton.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class InjectableWithSequenceSingleton {
	
	public static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(value=SINGLETON)
	public Injectable sequenceSingleton;
}
