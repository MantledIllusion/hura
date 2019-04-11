package com.mantledillusion.injection.hura.core.injection.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

public class InjectableWithInjectableField {
	
	@Inject
	public Injectable wiredField;
}
