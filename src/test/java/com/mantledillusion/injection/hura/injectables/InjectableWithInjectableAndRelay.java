package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.InjectableRelay;

public class InjectableWithInjectableAndRelay {

	@Inject(InjectableRelay.RELAY_SINGLETON_ID)
	private InjectableRelay relay;
	
	@Inject
	public InjectableWithFinalizingOperationOnRelay injectable;
	
	@Process
	private void activateRelay() {
		this.relay.relayActive = true;
	}
}
