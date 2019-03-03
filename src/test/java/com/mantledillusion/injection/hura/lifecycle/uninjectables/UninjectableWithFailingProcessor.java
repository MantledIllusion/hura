package com.mantledillusion.injection.hura.lifecycle.uninjectables;

import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PostConstruct;

public class UninjectableWithFailingProcessor {

	@PostConstruct
	private void process() {
		throw new RuntimeException("Exception during processing.");
	}
}
