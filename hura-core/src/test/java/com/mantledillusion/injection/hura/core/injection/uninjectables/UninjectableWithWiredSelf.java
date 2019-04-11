package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

public class UninjectableWithWiredSelf {

	@Inject
	public UninjectableWithWiredSelf self;
}
