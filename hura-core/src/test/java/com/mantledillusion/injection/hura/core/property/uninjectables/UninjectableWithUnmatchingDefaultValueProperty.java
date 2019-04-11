package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithUnmatchingDefaultValueProperty {

	@Property("property.key")
	@Matches("\\d")
	@DefaultValue("A")
	public String propertyValue;
}
