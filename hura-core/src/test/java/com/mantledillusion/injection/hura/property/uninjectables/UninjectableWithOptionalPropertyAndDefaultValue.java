package com.mantledillusion.injection.hura.property.uninjectables;

import com.mantledillusion.injection.hura.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.annotation.property.Property;

public class UninjectableWithOptionalPropertyAndDefaultValue {

	@Property("property.key")
	@DefaultValue("defaultValue")
	@Optional
	public String unannotatedProperty;
}
