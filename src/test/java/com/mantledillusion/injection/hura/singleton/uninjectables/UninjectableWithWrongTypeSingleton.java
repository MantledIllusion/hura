package com.mantledillusion.injection.hura.singleton.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithWrongTypeSingleton {
	
	private static final String SINGLETON = "singleton";

	@Inject(SINGLETON)
	public Injectable a;
	
	@Inject(SINGLETON)
	public String b;
}
