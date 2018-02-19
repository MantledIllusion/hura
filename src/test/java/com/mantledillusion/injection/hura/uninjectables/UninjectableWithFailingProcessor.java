package com.mantledillusion.injection.hura.uninjectables;

import com.mantledillusion.injection.hura.annotation.Process;

public class UninjectableWithFailingProcessor {

	@Process
	private void postProcess() {
		throw new RuntimeException("Exception during processing.");
	}
}
