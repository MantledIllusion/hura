package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Inject;

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
