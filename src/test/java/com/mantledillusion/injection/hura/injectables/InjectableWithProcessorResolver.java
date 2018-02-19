package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Processed;
import com.mantledillusion.injection.hura.annotation.Processed.PhasedProcessor;
import com.mantledillusion.injection.hura.misc.ResolverAtInspect;

@Processed(@PhasedProcessor(value=ResolverAtInspect.class, phase=Phase.INSPECT))
public class InjectableWithProcessorResolver {

	public String propertyValue;
}
