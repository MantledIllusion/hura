package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithNonStringPropertyField {

	@Property("property.key")
	public Integer intProperty;
}
