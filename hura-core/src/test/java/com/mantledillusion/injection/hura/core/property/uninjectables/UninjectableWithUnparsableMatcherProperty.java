package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithUnparsableMatcherProperty {

	@Resolve("property.key")
	@Matches("[!")
	public String propertyValue;
}
