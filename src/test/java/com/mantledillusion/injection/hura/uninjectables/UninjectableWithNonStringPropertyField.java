package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithNonStringPropertyField {

	@Property("property.key")
	public Integer intProperty;
}
