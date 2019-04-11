package com.mantledillusion.injection.hura.core.adjustment.injectables;

import com.mantledillusion.injection.hura.core.adjustment.misc.InjectableInterfaceExtension;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithExplicitIndependent;

public class InjectableWithExtendingAdjustment {

	@Inject
	@Adjust(extensions={InjectableInterfaceExtension.class})
	public InjectableWithExplicitIndependent extendedInjectable;
}
