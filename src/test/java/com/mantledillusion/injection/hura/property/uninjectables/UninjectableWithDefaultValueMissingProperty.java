package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.DefaultValue;

public class UninjectableWithDefaultValueMissingProperty {

	@DefaultValue("defaultValue")
	public String unannotatedProperty;
}
