package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithNonStringPropertyField {

	@Resolve("property.key")
	public Integer intProperty;
}
