package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithMatchedProperty {

	@Property("property.key")
	@Matches("\\d{2}")
	public String exactly2NumbersPropertyValue;
}
