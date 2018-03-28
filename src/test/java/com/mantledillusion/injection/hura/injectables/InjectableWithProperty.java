package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithProperty {

	public static final String PROPERTY_KEY = "property.key";
	
	@Property(PROPERTY_KEY)
	public String propertyValue;
}
