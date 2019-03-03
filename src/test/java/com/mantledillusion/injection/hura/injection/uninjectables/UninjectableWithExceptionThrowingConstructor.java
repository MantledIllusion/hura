package com.mantledillusion.injection.hura.injection.uninjectables;

public class UninjectableWithExceptionThrowingConstructor {

	public UninjectableWithExceptionThrowingConstructor() {
		throw new RuntimeException();
	}
}
