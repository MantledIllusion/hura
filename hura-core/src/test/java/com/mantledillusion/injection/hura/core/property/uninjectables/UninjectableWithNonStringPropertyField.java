package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

import java.io.Serializable;

public class UninjectableWithNonStringPropertyField {

	@Resolve("property.key")
	public Serializable intProperty;
}
