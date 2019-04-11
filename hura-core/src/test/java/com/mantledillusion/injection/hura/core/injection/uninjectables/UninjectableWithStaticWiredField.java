package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

public class UninjectableWithStaticWiredField {

	@Inject
	public static Injectable staticWiredField;
}
