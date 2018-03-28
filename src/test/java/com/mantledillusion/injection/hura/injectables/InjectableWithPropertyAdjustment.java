package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.annotation.Adjust;
import com.mantledillusion.injection.hura.annotation.Adjust.PropertyDef;
import com.mantledillusion.injection.hura.annotation.Inject;

public class InjectableWithPropertyAdjustment {

	public static final String ADJUSTED_PROPERTY_VALUE = "adjusted";

	@Inject
	@Adjust(properties = @PropertyDef(key = InjectableWithProperty.PROPERTY_KEY, value = ADJUSTED_PROPERTY_VALUE))
	public InjectableWithProperty propertiedInjectable;
}
