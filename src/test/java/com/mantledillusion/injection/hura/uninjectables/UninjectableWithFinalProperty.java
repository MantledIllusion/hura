package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithFinalProperty {

	@Property("property.key")
	public final String propertyValue = null;
}
