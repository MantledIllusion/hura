package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithUnparsableMatcherProperty {

	@Property(value="property.key", matcher="[!")
	public String propertyValue;
}
