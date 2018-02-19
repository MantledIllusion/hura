package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWithWiredSelf {

	@Inject
	public UninjectableWithWiredSelf self;
}
