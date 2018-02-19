package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithMatchedProperty {

	@Property(value="property.key", matcher="\\d{2}")
	public String exactly2NumbersPropertyValue;
}
