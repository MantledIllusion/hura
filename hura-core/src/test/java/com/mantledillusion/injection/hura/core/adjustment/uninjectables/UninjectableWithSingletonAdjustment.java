package com.mantledillusion.injection.hura.core.adjustment.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;

public class UninjectableWithSingletonAdjustment {

	@Inject
	@Adjust
	@Qualifier("qualifier")
	public Injectable injectable;
}
