package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithStaticProperty {

	@Property("property.key")
	public static String propertyValue;
}
