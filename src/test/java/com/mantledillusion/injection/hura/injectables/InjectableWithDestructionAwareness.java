package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Process;

public class InjectableWithDestructionAwareness {

	public boolean wasDestructed = false;
	
	@Process(Phase.DESTROY)
	private void destruct() {
		this.wasDestructed = true;
	}
}
