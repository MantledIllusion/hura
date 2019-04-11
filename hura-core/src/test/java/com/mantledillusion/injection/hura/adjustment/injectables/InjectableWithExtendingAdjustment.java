package com.mantledillusion.injection.hura.adjustment.injectables;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.injection.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.adjustment.misc.InjectableInterfaceExtension;

public class InjectableWithExtendingAdjustment {

	@Inject
	@Adjust(extensions={InjectableInterfaceExtension.class})
	public InjectableWithExplicitIndependent extendedInjectable;
}
