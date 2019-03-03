package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithoutPropertyKey {

	@Property("")
	public String propertyValue;
}
