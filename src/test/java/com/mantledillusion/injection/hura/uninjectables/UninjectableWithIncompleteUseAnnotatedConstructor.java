package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithIncompleteUseAnnotatedConstructor {
	
	public UninjectableWithIncompleteUseAnnotatedConstructor() {
		
	}
	
	@Construct
	public UninjectableWithIncompleteUseAnnotatedConstructor(@Inject Injectable a, Injectable b) {
		
	}
}
