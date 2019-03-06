package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.annotation.injection.SingletonMode;

public class UninjectableWithBeanWithWiringContextSensitiveSingleton {
	
	private static final String SINGLETON = "subBean";
	
	@Inject
	@Qualifier(value = SINGLETON, mode = SingletonMode.GLOBAL)
    public UninjectableWithWiringContextSensitiveSingleton subBean;
}
