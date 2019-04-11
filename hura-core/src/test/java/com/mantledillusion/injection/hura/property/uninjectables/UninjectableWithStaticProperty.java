package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithStaticProperty {

	@Property("property.key")
	public static String propertyValue;
}
