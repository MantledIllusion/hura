package com.mantledillusion.injection.hura.context.uninjectables;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class UninjectableWithContextWithoutQualifier {

	@Inject(StringUtils.EMPTY)
	public ExampleContext context;
	
}
