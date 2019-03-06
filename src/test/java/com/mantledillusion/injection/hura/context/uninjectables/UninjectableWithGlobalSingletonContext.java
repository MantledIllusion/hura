package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class UninjectableWithGlobalSingletonContext {

	@Inject
    @Qualifier(ExampleContext.SINGLETON)
    public ExampleContext context;
}
