package com.mantledillusion.injection.hura.core.context.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.context.misc.ExampleContext;

public class UninjectableWithGlobalSingletonContext {

	@Inject
    @Qualifier(ExampleContext.SINGLETON)
    public ExampleContext context;
}
