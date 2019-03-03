package com.mantledillusion.injection.hura.adjustment.injectables;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.MappingDef;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithSequenceSingleton;

public class InjectableWithMappingAdjustment {

	public static final String SOME_QUALIFIER = "someUnfittingQualifier";

	@Inject
	@Adjust(mappings = @MappingDef(base = InjectableWithSequenceSingleton.SINGLETON, target = SOME_QUALIFIER))
	public InjectableWithSequenceSingleton singletonedInjectable;
}
