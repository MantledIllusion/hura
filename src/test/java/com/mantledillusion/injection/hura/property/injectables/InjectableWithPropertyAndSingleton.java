package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithPropertyAndSingleton {

	@Property("property.key")
	public String propertyValue;
	
	@Inject
	@Qualifier("qualifier")
	public String singleton;
}