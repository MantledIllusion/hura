package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.ExampleContext;

public class UninjectableWithGlobalSingletonContext {

	@Inject(ExampleContext.SINGLETON)
	@Global
	public ExampleContext context;
}
