package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class InjectableWithMatchedProperty {

	@Property("property.key")
	@Matches("\\d{2}")
	public String exactly2NumbersPropertyValue;
}
