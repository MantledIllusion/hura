package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;

public class UninjectableWithOptionalInjectableAndMissingInjectAnnotation {

	@Optional
	public Injectable injectable;
}
