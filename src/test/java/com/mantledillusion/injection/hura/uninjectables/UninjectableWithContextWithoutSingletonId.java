package com.mantledillusion.injection.hura.uninjectables;

import org.apache.commons.lang3.StringUtils;

import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.misc.ExampleContext;

public class UninjectableWithContextWithoutSingletonId {

	@Inject(StringUtils.EMPTY)
	public ExampleContext context;
	
}
