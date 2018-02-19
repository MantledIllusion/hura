package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.InjectableRelay;

public class InjectableWithFinalizingOperationOnRelay {
	
	@Inject(InjectableRelay.RELAY_SINGLETON_ID)
	private InjectableRelay relay;
	
	public boolean hasRun;
	
	@Process(Phase.FINALIZE)
	private void run() {
		if (this.relay.relayActive) {
			this.hasRun = true;
		}
	}
}
