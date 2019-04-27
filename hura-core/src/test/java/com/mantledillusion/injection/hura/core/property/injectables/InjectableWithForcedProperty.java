package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithForcedProperty {

	@Resolve("${property.key}")
	public String forcedPropertyValue;
}
