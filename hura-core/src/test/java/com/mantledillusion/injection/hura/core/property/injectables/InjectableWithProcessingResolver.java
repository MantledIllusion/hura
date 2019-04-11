package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;

public class InjectableWithProcessingResolver {

	public String propertyValue;

	@PostInject
	private void process(TemporalInjectorCallback callback) {
		this.propertyValue = callback.resolve("property.key");
	}
}
