package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithProperty {

	@Property("property.key")
	public String propertyValue;
}
