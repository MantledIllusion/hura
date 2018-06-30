package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWithBeanWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "subBean";
	
	@Inject(SINGLETON)
	@Global
	public UninjectableWithWiringContextSensitiveSingleton subBean;
}
