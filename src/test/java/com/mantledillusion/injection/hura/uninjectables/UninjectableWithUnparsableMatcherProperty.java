package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Matches;
import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithUnparsableMatcherProperty {

	@Property("property.key")
	@Matches("[!")
	public String propertyValue;
}
