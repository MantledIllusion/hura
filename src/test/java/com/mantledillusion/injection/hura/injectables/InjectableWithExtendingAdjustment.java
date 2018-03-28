package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.InjectableInterfaceExtension;

public class InjectableWithExtendingAdjustment {

	@Inject
	@Adjust(extensions={InjectableInterfaceExtension.class})
	public InjectableWithExplicitIndependent extendedInjectable;
}
