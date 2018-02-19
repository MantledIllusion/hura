package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.InjectableInterfaceExtension;

public class InjectableWithExtension {

	@Inject(extensions={InjectableInterfaceExtension.class})
	public InjectableWithExplicitSingleton extendedInjectable;
}
