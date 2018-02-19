package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithUnmatchingDefaultValueProperty {

	@Property(value="property.key", matcher="\\d", useDefault=true, defaultValue="A")
	public String propertyValue;
}
