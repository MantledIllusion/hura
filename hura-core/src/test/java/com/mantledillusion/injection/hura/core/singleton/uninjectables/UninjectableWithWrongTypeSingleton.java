package com.mantledillusion.injection.hura.core.singleton.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class UninjectableWithWrongTypeSingleton {
	
	private static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public Injectable a;
	
	@Inject
	@Qualifier(SINGLETON)
	public String b;
}
