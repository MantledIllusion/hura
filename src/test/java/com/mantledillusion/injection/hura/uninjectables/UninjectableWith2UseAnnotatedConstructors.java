package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWith2UseAnnotatedConstructors {
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors() {
		
	}
	
	@Construct
	public UninjectableWith2UseAnnotatedConstructors(@Inject Injectable onlyWiredThroughUseAnnotatedConstructor) {
		
	}
}
