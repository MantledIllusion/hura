package com.mantledillusion.injection.hura.adjustment.misc;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.InjectableInterface;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.adjustment.injectables.InjectableAlternative;

public class InjectableInterfaceExtension implements BlueprintTemplate {
	
	@Define
	private BeanAllocation<InjectableInterface> allocate() {
		return BeanAllocation.allocateToType(InjectableAlternative.class);
	}
}
