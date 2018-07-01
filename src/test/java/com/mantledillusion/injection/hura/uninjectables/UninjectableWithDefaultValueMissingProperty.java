package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.DefaultValue;

public class UninjectableWithDefaultValueMissingProperty {

	@DefaultValue("defaultValue")
	public String unannotatedProperty;
}
