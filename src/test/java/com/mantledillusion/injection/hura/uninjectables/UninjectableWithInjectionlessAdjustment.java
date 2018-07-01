package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithInjectionlessAdjustment {

	@Adjust
	public Injectable injectable;
}
