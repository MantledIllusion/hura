package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class UninjectableWithGlobalSingletonContext {

	@Inject(ExampleContext.SINGLETON)
	@Global
	public ExampleContext context;
}
