package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.BeanAllocation;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.annotation.Define;
import com.mantledillusion.injection.hura.injectables.Injectable2;

public class InjectableInterfaceExtension implements BlueprintTemplate {
	
	@Define
	private BeanAllocation<InjectableInterface> allocate() {
		return BeanAllocation.allocateToType(Injectable2.class);
	}
}
