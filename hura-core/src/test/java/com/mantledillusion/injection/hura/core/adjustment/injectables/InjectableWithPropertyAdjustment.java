package com.mantledillusion.injection.hura.core.adjustment.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProperty;

public class InjectableWithPropertyAdjustment {

	public static final String ADJUSTED_PROPERTY_VALUE = "adjusted";

	@Inject
	@Adjust(properties = @Adjust.PropertyDef(key = InjectableWithProperty.PROPERTY_KEY, value = ADJUSTED_PROPERTY_VALUE))
	public InjectableWithProperty propertiedInjectable;
}
