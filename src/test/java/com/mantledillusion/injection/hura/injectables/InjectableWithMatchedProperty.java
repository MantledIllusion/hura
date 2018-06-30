package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Matches;
import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithMatchedProperty {

	@Property("property.key")
	@Matches("\\d{2}")
	public String exactly2NumbersPropertyValue;
}
