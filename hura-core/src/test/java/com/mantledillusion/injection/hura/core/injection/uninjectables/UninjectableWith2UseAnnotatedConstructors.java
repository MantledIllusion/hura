package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

public class UninjectableWith2UseAnnotatedConstructors {
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors() {
		
	}
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors(@Inject Injectable onlyWiredThroughUseAnnotatedConstructor) {
		
	}
}
