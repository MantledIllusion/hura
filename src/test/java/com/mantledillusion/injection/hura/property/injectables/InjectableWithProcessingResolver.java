package com.mantledillusion.injection.hura.property.injectables;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostInject;

public class InjectableWithProcessingResolver {

	public String propertyValue;

	@PostInject
	private void process(TemporalInjectorCallback callback) {
		this.propertyValue = callback.resolve("property.key");
	}
}
