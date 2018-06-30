package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.DefaultValue;
import com.mantledillusion.injection.hura.annotation.Matches;
import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithUnmatchingDefaultValueProperty {

	@Property("property.key")
	@Matches("\\d")
	@DefaultValue("A")
	public String propertyValue;
}
