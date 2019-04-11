package com.mantledillusion.injection.hura.core.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostConstruct;

public class UninjectableWithFailingProcessor {

	@PostConstruct
	private void process() {
		throw new RuntimeException("Exception during processing.");
	}
}
