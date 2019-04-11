package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Matches;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithUnparsableMatcherProperty {

	@Property("property.key")
	@Matches("[!")
	public String propertyValue;
}
