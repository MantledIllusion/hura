package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class InjectableWithForcedProperty {

	@Property("property.key")
	public String forcedPropertyValue;
}
