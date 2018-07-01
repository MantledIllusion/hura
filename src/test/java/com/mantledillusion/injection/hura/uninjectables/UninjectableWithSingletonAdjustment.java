package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithSingletonAdjustment {

	@Inject("qualifier")
	@Adjust
	public Injectable injectable;
}
