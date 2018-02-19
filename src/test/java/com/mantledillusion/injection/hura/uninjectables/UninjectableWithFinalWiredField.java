package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.injectables.Injectable;

public class UninjectableWithFinalWiredField {

	@Inject
	public final Injectable finalWiredField = null;
}
