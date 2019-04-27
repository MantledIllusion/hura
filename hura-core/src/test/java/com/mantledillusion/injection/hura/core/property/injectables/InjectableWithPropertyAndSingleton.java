package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithPropertyAndSingleton {

	@Resolve("${property.key}")
	public String propertyValue;
	
	@Inject
	@Qualifier("qualifier")
	public String singleton;
}
