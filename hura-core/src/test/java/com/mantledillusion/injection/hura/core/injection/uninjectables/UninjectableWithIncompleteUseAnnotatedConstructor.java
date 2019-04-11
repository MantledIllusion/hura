package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

public class UninjectableWithIncompleteUseAnnotatedConstructor {
	
	public UninjectableWithIncompleteUseAnnotatedConstructor() {
		
	}
	
	@Construct
	public UninjectableWithIncompleteUseAnnotatedConstructor(@Inject Injectable a, Injectable b) {
		
	}
}
