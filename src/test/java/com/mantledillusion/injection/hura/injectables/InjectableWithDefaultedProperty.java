package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.DefaultValue;
import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithDefaultedProperty {

	@Property("property.key")
	@DefaultValue("defaultValue")
	public String propertyValue;
}
