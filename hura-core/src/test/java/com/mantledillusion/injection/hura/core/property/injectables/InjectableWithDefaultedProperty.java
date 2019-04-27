package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithDefaultedProperty {

	@Resolve("${property.key:defaultValue}")
	public String propertyValue;
}
