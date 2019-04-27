package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithOptionalPropertyAndDefaultValue {

	public static final String DEFAULT_VALUE = "defaultValue";

	@Resolve("${property.key:"+DEFAULT_VALUE+"}")
	@Optional
	public String property;
}
