package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithFinalProperty {

	@Property("property.key")
	public final String propertyValue = null;
}
