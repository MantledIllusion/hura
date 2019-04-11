package com.mantledillusion.injection.hura.context.uninjectables;

import com.mantledillusion.injection.hura.annotation.injection.Qualifier;
import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class UninjectableWithContextWithoutQualifier {

	@Inject
	@Qualifier(StringUtils.EMPTY)
	public ExampleContext context;
	
}
