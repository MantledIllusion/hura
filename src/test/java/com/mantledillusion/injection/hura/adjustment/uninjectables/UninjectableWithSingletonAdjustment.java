package com.mantledillusion.injection.hura.adjustment.uninjectables;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithSingletonAdjustment {

	@Inject("qualifier")
	@Adjust
	public Injectable injectable;
}
