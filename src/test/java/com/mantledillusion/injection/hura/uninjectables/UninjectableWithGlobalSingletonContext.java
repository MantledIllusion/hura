package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;
import com.mantledillusion.injection.hura.misc.ExampleContext;

public class UninjectableWithGlobalSingletonContext {

	@Inject(value=ExampleContext.SINGLETON, singletonMode=SingletonMode.GLOBAL)
	public ExampleContext context;
}
