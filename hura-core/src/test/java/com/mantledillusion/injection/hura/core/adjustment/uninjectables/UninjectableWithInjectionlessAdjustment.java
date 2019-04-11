package com.mantledillusion.injection.hura.core.adjustment.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;

public class UninjectableWithInjectionlessAdjustment {

	@Adjust
	public Injectable injectable;
}
