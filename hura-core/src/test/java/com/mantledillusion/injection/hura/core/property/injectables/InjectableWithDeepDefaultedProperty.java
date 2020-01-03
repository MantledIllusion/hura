package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithDeepDefaultedProperty {

	@Resolve("${property.key:${property.otherKey:defaultValue}}")
	public String propertyValue;
}
