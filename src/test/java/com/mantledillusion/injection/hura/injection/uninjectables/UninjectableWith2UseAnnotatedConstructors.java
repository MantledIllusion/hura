package com.mantledillusion.injection.hura.injection.uninjectables;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWith2UseAnnotatedConstructors {
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors() {
		
	}
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors(@Inject Injectable onlyWiredThroughUseAnnotatedConstructor) {
		
	}
}
