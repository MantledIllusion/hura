package com.mantledillusion.injection.hura.injection.uninjectables;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithIncompleteUseAnnotatedConstructor {
	
	public UninjectableWithIncompleteUseAnnotatedConstructor() {
		
	}
	
	@Construct
	public UninjectableWithIncompleteUseAnnotatedConstructor(@Inject Injectable a, Injectable b) {
		
	}
}
