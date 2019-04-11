package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;

public class UninjectableWithMatcherAndMissingProperty {

	@Matches(".*")
	public String noProperty;
}
