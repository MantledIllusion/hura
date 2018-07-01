package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithGlobalSingletonWithoutInject {

	@Global
	public Injectable singleton;
}
