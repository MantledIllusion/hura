package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithDefaultedProperty {

	@Property(value="property.key", useDefault=true, defaultValue="defaultValue")
	public String propertyValue;
}
