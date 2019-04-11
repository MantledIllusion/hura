package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithNonStringPropertyField {

	@Property("property.key")
	public Integer intProperty;
}
