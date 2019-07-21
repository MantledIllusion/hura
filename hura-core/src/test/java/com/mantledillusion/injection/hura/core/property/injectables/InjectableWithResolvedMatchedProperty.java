package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.property.Matches;
import com.mantledillusion.injection.hura.core.annotation.property.Resolve;

public class InjectableWithResolvedMatchedProperty {

	@Resolve("${property.key}")
	@Matches("${matcher}")
	public String value;
}
