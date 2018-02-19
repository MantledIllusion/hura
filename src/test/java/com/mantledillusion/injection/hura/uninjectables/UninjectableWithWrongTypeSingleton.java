package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithWrongTypeSingleton {
	
	private static final String SINGLETON = "singleton";

	@Inject(SINGLETON)
	public Injectable a;
	
	@Inject(SINGLETON)
	public String b;
}
