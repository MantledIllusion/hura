package com.mantledillusion.injection.hura.core.adjustment.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.core.singleton.injectables.InjectableWithSequenceSingleton;

public class InjectableWithMappingAdjustment {

	public static final String SOME_QUALIFIER = "someUnfittingQualifier";

	@Inject
	@Adjust(mappings = @Adjust.MappingDef(base = InjectableWithSequenceSingleton.SINGLETON, target = SOME_QUALIFIER))
	public InjectableWithSequenceSingleton singletonedInjectable;
}
