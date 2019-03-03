package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithFinalProperty {

	@Property("property.key")
	public final String propertyValue = null;
}
