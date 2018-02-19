package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithForcedProperty {

	@Property(value="property.key", forced=true)
	public String forcedPropertyValue;
}
