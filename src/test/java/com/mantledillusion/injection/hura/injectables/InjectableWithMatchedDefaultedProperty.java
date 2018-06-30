package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.DefaultValue;
import com.mantledillusion.injection.hura.annotation.Matches;
import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithMatchedDefaultedProperty {

	public static final String DEFAULT_VALUE = "10";
	
	@Property("property.key")
	@Matches("\\d{2}")
	@DefaultValue(DEFAULT_VALUE)
	public String exactly2NumbersPropertyValue;
}
