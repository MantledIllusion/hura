package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithFinalProperty {

	@Resolve("property.key")
	public final String propertyValue = null;
}
