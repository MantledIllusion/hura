package com.mantledillusion.injection.hura.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct;

public class UninjectableWithWrongTypeParameterProcessMethod {

	@PostConstruct
	private void postProcess(String param) {
		
	}
}
