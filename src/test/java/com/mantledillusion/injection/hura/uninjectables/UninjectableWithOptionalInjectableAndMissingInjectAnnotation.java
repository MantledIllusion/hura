package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithOptionalInjectableAndMissingInjectAnnotation {

	@Optional
	public Injectable injectable;
}
