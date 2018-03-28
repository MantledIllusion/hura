package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.annotation.Adjust.MappingDef;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectabeWithMappingAdjustment {

	public static final String SOME_SINGLETON_ID = "someUnfittingSingletonId";

	@Inject
	@Adjust(mappings = @MappingDef(base = InjectableWithSequenceSingleton.SINGLETON, target = SOME_SINGLETON_ID))
	public InjectableWithSequenceSingleton singletonedInjectable;
}
