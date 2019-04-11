package com.mantledillusion.injection.hura.core.singleton.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;

public class UninjectableWithSingletonWithoutInject {

	@Qualifier("qualifier")
	public Injectable singleton;
}
