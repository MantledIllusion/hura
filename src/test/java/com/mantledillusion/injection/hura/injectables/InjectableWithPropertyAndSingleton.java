package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithPropertyAndSingleton {

	@Property("property.key")
	public String propertyValue;
	
	@Inject("qualifier")
	public String singleton;
}
