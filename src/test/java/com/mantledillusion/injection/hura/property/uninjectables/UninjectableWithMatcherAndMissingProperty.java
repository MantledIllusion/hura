package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.Matches;

public class UninjectableWithMatcherAndMissingProperty {

	@Matches(".*")
	public String noProperty;
}
