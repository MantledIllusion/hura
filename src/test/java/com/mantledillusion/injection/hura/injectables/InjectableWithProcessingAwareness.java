package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Process;

public class InjectableWithProcessingAwareness extends InjectableWithProcessableFields {
	
	@Process(Phase.INSPECT)
	private void inspect() {
		this.occurredPhases.add(Phase.INSPECT);
		this.injectableAtInspect = injectable;
	}
	
	@Process(Phase.CONSTRUCT)
	private void construct() {
		this.occurredPhases.add(Phase.CONSTRUCT);
		this.injectableAtConstruct = injectable;
	}
	
	@Process(Phase.INJECT)
	private void inject() {
		this.occurredPhases.add(Phase.INJECT);
		this.injectableAtInject = injectable;
	}
	
	@Process(Phase.FINALIZE)
	private void finalizee() {
		this.occurredPhases.add(Phase.FINALIZE);
		this.injectableAtFinalize = injectable;
	}
	
	@Process(Phase.DESTROY)
	private void destroy() {
		this.occurredPhases.add(Phase.DESTROY);
		this.injectableAtDestroy = injectable;
	}
}
