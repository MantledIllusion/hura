package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class UninjectableWithUnmatchingDefaultValueProperty {

	@Resolve("${property.key:A}")
	@Matches("\\d")
	public String propertyValue;
}
