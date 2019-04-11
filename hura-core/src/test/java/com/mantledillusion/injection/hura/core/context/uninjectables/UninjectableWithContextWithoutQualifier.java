package com.mantledillusion.injection.hura.core.context.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.context.misc.ExampleContext;
import org.apache.commons.lang3.StringUtils;

public class UninjectableWithContextWithoutQualifier {

	@Inject
	@Qualifier(StringUtils.EMPTY)
	public ExampleContext context;
	
}
