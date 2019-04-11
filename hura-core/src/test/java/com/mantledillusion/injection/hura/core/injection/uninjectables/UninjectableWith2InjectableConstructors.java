package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

public class UninjectableWith2InjectableConstructors {

	public UninjectableWith2InjectableConstructors(@Inject String wiredParamConstructorA) {
		
	}

	public UninjectableWith2InjectableConstructors(@Inject Integer wiredParamConstructorB) {
		
	}
}
