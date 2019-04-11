package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithStaticProperty {

	@Property("property.key")
	public static String propertyValue;
}
