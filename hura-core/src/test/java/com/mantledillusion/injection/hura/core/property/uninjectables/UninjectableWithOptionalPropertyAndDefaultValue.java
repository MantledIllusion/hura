package com.mantledillusion.injection.hura.core.property.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.DefaultValue;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

public class UninjectableWithOptionalPropertyAndDefaultValue {

	@Property("property.key")
	@DefaultValue("defaultValue")
	@Optional
	public String unannotatedProperty;
}
