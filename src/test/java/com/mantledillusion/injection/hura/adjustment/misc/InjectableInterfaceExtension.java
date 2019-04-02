package com.mantledillusion.injection.hura.adjustment.misc;

import com.mantledillusion.injection.hura.Blueprint;
import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.adjustment.injectables.InjectableAlternative;

public class InjectableInterfaceExtension implements Blueprint {
	
	@Define
	private TypeAllocation allocate() {
		return TypeAllocation.allocateToType(InjectableInterface.class, InjectableAlternative.class);
	}
}
