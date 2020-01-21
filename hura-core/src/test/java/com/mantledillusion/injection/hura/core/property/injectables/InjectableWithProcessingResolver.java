package com.mantledillusion.injection.hura.core.property.injectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;
import com.mantledillusion.injection.hura.core.service.ResolvingProvider;

public class InjectableWithProcessingResolver {

	public String propertyValue;

	@PostInject
	private void process(ResolvingProvider resolvingProvider) {
		this.propertyValue = resolvingProvider.resolve("${property.key}");
	}
}
