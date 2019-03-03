package com.mantledillusion.injection.hura.injection.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class UninjectableWithFinalWiredField {

	@Inject
	public final Injectable finalWiredField = null;
}
