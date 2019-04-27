package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithResolvableConstructor {

	public final String propertyValue;

	public InjectableWithResolvableConstructor(@Resolve("${property.key}") String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
}
