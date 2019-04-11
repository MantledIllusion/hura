package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.DefaultValue;

public class UninjectableWithDefaultValueMissingProperty {

	@DefaultValue("defaultValue")
	public String unannotatedProperty;
}
