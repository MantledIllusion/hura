package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class InjectableWithMatchedDefaultedProperty {

	public static final String DEFAULT_VALUE = "10";
	
	@Property("property.key")
	@Matches("\\d{2}")
	@DefaultValue(DEFAULT_VALUE)
	public String exactly2NumbersPropertyValue;
}