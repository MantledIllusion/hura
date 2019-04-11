package com.mantledillusion.injection.hura.adjustment.uninjectables;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithInjectionlessAdjustment {

	@Adjust
	public Injectable injectable;
}
