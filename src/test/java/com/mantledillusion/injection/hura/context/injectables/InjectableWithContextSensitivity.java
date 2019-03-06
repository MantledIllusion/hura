package com.mantledillusion.injection.hura.context.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class InjectableWithContextSensitivity {
	
	@Inject
	@Qualifier(ExampleContext.SINGLETON)
	public ExampleContext context;
}
