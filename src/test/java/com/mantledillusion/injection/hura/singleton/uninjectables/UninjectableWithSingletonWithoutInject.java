package com.mantledillusion.injection.hura.singleton.uninjectables;

import com.mantledillusion.injection.hura.Injectable;
import com.mantledillusion.injection.hura.annotation.injection.Qualifier;

public class UninjectableWithSingletonWithoutInject {

	@Qualifier("qualifier")
	public Injectable singleton;
}
