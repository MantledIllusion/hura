package com.mantledillusion.injection.hura.core.context.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.context.injectables.InjectableWithContextSensitivity;

public class UninjectableWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "singleton";
	
	@Inject
	@Qualifier(SINGLETON)
	public InjectableWithContextSensitivity wiringContextSensitiveSingleton;
}
