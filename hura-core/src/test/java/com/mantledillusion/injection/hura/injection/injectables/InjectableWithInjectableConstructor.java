package com.mantledillusion.injection.hura.injection.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.Injectable;

public class InjectableWithInjectableConstructor {

	public final Injectable wiredThroughConstructor;
	
	public InjectableWithInjectableConstructor(@Inject Injectable wiredThroughConstructor) {
		this.wiredThroughConstructor = wiredThroughConstructor;
	}
}
