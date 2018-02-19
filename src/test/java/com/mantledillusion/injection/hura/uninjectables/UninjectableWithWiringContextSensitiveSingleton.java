package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.InjectableWithContextSensitivity;

public class UninjectableWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "singleton";
	
	@Inject(SINGLETON)
	public InjectableWithContextSensitivity wiringContextSensitiveSingleton;
}
