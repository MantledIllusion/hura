package com.mantledillusion.injection.hura.core.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;

public class UninjectableWithWrongTypeParameterProcessMethod {

	@PostConstruct
	private void postProcess(String param) {
		
	}
}
