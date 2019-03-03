package com.mantledillusion.injection.hura.singleton.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithIndependentGlobalAnnotatedInjectable {

	@Inject
	@Global
	public Injectable injectable;
}
