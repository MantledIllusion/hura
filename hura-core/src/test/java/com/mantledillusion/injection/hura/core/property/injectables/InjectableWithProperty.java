package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class InjectableWithProperty {

	public static final String PROPERTY_KEY = "property.key";
	
	@Property(PROPERTY_KEY)
	@Optional
	public String propertyValue;
}
