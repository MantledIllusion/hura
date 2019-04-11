package com.mantledillusion.injection.hura.injection.uninjectables;

import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithOptionalInjectableAndMissingInjectAnnotation {

	@Optional
	public Injectable injectable;
}
