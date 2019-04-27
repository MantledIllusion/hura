package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithStaticProperty {

	@Resolve("property.key")
	public static String propertyValue;
}
