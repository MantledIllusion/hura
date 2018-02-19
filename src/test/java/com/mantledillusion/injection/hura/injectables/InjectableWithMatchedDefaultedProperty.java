package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class InjectableWithMatchedDefaultedProperty {

	public static final String DEFAULT_VALUE = "10";
	
	@Property(value="property.key", matcher="\\d{2}", useDefault=true, defaultValue=DEFAULT_VALUE)
	public String exactly2NumbersPropertyValue;
}
