package com.mantledillusion.injection.hura.injection.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.InjectableInterface;

public class InjectableWithExplicitIndependent {
	
	@Inject
	@Optional
	public InjectableInterface explicitInjectable;
}
