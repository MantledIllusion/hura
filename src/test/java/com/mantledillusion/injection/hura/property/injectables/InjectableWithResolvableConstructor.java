package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithResolvableConstructor {

	public final String propertyValue;

	public InjectableWithResolvableConstructor(@Property("property.key") String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
}
