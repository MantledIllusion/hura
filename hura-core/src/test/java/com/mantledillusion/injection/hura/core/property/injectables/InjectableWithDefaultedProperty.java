package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class InjectableWithDefaultedProperty {

	@Property("property.key")
	@DefaultValue("defaultValue")
	public String propertyValue;
}
