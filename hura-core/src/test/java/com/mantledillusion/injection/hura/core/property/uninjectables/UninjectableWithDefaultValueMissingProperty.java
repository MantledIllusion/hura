package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithDefaultValueMissingProperty {

	@Resolve("${defaultValue}")
	public String unannotatedProperty;
}
