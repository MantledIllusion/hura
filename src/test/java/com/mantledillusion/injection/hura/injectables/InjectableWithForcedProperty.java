package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithForcedProperty {

	@Property("property.key")
	public String forcedPropertyValue;
}
