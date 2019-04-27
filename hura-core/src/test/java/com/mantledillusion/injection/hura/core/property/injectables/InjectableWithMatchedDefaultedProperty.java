package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithMatchedDefaultedProperty {

	public static final String DEFAULT_VALUE = "10";
	
	@Resolve("${property.key:"+DEFAULT_VALUE+"}")
	@Matches("\\d{2}")
	public String exactly2NumbersPropertyValue;
}
