package com.mantledillusion.injection.hura.singleton.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Global;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithGlobalSingletonWithoutInject {

	@Global
	public Injectable singleton;
}
