package com.mantledillusion.injection.hura.core.injection.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;

public class InjectableWithExplicitIndependent {
	
	@Inject
	@Optional
	public InjectableInterface explicitInjectable;
}
