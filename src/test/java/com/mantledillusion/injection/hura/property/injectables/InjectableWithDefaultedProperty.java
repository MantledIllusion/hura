package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithDefaultedProperty {

	@Property("property.key")
	@DefaultValue("defaultValue")
	public String propertyValue;
}
