package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithResolvableConstructor {

	public final String propertyValue;

	public InjectableWithResolvableConstructor(@Property("property.key") String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
}
