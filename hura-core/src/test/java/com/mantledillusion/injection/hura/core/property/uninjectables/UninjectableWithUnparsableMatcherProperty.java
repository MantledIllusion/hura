package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithUnparsableMatcherProperty {

	@Property("property.key")
	@Matches("[!")
	public String propertyValue;
}
