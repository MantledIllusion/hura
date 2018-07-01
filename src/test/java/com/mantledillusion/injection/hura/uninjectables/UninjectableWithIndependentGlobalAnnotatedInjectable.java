package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithIndependentGlobalAnnotatedInjectable {

	@Inject
	@Global
	public Injectable injectable;
}
