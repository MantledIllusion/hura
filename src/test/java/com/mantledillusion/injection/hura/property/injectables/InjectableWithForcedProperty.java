package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithForcedProperty {

	@Property("property.key")
	public String forcedPropertyValue;
}
