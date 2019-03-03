package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.context.injectables.InjectableWithContextSensitivity;

public class UninjectableWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	public InjectableWithContextSensitivity wiringContextSensitiveSingleton;
}
