package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWith2InjectableConstructors {

	public UninjectableWith2InjectableConstructors(@Inject String wiredParamConstructorA) {
		
	}

	public UninjectableWith2InjectableConstructors(@Inject Integer wiredParamConstructorB) {
		
	}
}
