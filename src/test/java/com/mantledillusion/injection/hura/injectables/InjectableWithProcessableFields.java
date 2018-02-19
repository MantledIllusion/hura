package com.mantledillusion.injection.hura.injectables;

import java.util.ArrayList;
import java.util.List;

import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithProcessableFields {

	@Inject
	public Injectable injectable;

	public List<Processor.Phase> occurredPhases = new ArrayList<>();
	
	public Injectable injectableAtInspect;
	public Injectable injectableAtConstruct;
	public Injectable injectableAtInject;
	public Injectable injectableAtDestroy;
	public Injectable injectableAtFinalize;
}
