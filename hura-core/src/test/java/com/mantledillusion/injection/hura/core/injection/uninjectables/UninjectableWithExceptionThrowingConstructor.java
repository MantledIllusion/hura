package com.mantledillusion.injection.hura.core.injection.uninjectables;

public class UninjectableWithExceptionThrowingConstructor {

	public UninjectableWithExceptionThrowingConstructor() {
		throw new RuntimeException();
	}
}
