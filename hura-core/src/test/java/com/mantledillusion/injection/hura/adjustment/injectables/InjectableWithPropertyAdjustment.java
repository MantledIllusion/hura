package com.mantledillusion.injection.hura.adjustment.injectables;

import com.mantledillusion.injection.hura.annotation.instruction.Adjust;
import com.mantledillusion.injection.hura.annotation.instruction.Adjust.PropertyDef;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProperty;

public class InjectableWithPropertyAdjustment {

	public static final String ADJUSTED_PROPERTY_VALUE = "adjusted";

	@Inject
	@Adjust(properties = @PropertyDef(key = InjectableWithProperty.PROPERTY_KEY, value = ADJUSTED_PROPERTY_VALUE))
	public InjectableWithProperty propertiedInjectable;
}
