package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;

public class UninjectableWithBeanWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "subBean";
	
	@Inject(value=SINGLETON, singletonMode=SingletonMode.GLOBAL)
	public UninjectableWithWiringContextSensitiveSingleton subBean;
}
