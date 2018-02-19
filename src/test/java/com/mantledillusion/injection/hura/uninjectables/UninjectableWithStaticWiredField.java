package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithStaticWiredField {

	@Inject
	public static Injectable staticWiredField;
}
