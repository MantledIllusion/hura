package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithProperty {

	public static final String PROPERTY_KEY = "property.key";
	
	@Property(PROPERTY_KEY)
	@Optional
	public String propertyValue;
}
