package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.context.injectables.InjectableWithContextSensitivity;

public class UninjectableWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(SINGLETON)
	public InjectableWithContextSensitivity wiringContextSensitiveSingleton;
}
