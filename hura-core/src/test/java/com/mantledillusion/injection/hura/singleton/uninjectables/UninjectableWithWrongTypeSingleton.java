package com.mantledillusion.injection.hura.singleton.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class UninjectableWithWrongTypeSingleton {
	
	private static final String SINGLETON = "singleton";

	@Inject
	@Qualifier(SINGLETON)
	public Injectable a;
	
	@Inject
	@Qualifier(SINGLETON)
	public String b;
}
