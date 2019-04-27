package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithMatchedProperty {

	@Resolve("${property.key}")
	@Matches("\\d{2}")
	public String exactly2NumbersPropertyValue;
}
