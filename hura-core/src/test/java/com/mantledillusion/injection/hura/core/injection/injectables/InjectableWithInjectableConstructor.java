package com.mantledillusion.injection.hura.core.injection.injectables;

import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

public class InjectableWithInjectableConstructor {

	public final Injectable wiredThroughConstructor;
	
	public InjectableWithInjectableConstructor(@Inject Injectable wiredThroughConstructor) {
		this.wiredThroughConstructor = wiredThroughConstructor;
	}
}
