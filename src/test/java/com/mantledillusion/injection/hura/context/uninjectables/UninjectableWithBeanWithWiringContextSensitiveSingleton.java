package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class UninjectableWithBeanWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "subBean";
	
	@Inject(SINGLETON)
	@Global
	public UninjectableWithWiringContextSensitiveSingleton subBean;
}
