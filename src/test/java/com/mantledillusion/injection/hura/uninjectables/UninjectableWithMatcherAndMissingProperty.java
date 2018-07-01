package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Matches;

public class UninjectableWithMatcherAndMissingProperty {

	@Matches(".*")
	public String noProperty;
}
