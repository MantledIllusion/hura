package com.mantledillusion.injection.hura.core.adjustment.misc;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.adjustment.injectables.InjectableAlternative;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;

public class InjectableInterfaceExtension implements Blueprint {
	
	@Define
	private TypeAllocation allocate() {
		return TypeAllocation.allocateToType(InjectableInterface.class, InjectableAlternative.class);
	}
}
