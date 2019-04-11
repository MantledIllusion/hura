package com.mantledillusion.injection.hura.core.injection.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;

public class InjectableWithAnnotatedConstructor {

	public final Injectable onlyWiredThroughUseAnnotatedConstructor;
	
	public InjectableWithAnnotatedConstructor() {
		onlyWiredThroughUseAnnotatedConstructor = null;
	}
	
	@Construct
	private InjectableWithAnnotatedConstructor(@Inject Injectable onlyWiredThroughUseAnnotatedConstructor) {
		this.onlyWiredThroughUseAnnotatedConstructor = onlyWiredThroughUseAnnotatedConstructor;
	}
}
