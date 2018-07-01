package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.DefaultValue;
import com.mantledillusion.injection.hura.annotation.Optional;
import com.mantledillusion.injection.hura.annotation.Property;

public class UninjectableWithOptionalPropertyAndDefaultValue {

	@Property("property.key")
	@DefaultValue("defaultValue")
	@Optional
	public String unannotatedProperty;
}
