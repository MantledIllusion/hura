package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithUnmatchingDefaultValueProperty {

	@Property("property.key")
	@Matches("\\d")
	@DefaultValue("A")
	public String propertyValue;
}
