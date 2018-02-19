package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithInjectableConstructor {

	public final Injectable wiredThroughConstructor;
	
	public InjectableWithInjectableConstructor(@Inject Injectable wiredThroughConstructor) {
		this.wiredThroughConstructor = wiredThroughConstructor;
	}
}
