package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.ExampleContext;

public class InjectableWithContextSensitivity {
	
	@Inject(ExampleContext.SINGLETON)
	public ExampleContext context;
}
