package com.mantledillusion.injection.hura.uninjectables;

public class UninjectableWithExceptionThrowingConstructor {

	public UninjectableWithExceptionThrowingConstructor() {
		throw new RuntimeException();
	}
}
