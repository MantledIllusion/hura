package com.mantledillusion.injection.hura.injection.injectables;

import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

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
