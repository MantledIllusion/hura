package com.mantledillusion.injection.hura.core.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;

public class UninjectableWithStaticProcessMethod {

	@PostConstruct
	private static void process() {
		
	}
}
