package com.mantledillusion.injection.hura.injection.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class UninjectableWith2InjectableConstructors {

	public UninjectableWith2InjectableConstructors(@Inject String wiredParamConstructorA) {
		
	}

	public UninjectableWith2InjectableConstructors(@Inject Integer wiredParamConstructorB) {
		
	}
}
