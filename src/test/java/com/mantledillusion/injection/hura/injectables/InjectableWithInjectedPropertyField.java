package com.mantledillusion.injection.hura.injectables;

import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Processed;
import com.mantledillusion.injection.hura.annotation.Processed.PhasedProcessor;
import com.mantledillusion.injection.hura.annotation.Property;
import com.mantledillusion.injection.hura.misc.TypeInjectionResolvingProcessorAtConstruct;
import com.mantledillusion.injection.hura.misc.TypeInjectionResolvingProcessorAtInject;

@Processed({@PhasedProcessor(value=TypeInjectionResolvingProcessorAtConstruct.class, phase=Phase.CONSTRUCT),
	@PhasedProcessor(value=TypeInjectionResolvingProcessorAtInject.class, phase=Phase.INJECT)})
public class InjectableWithInjectedPropertyField {

	@Property("property.key")
	@Inject("propertyQualifier")
	public String injectableProperty;
	
	public String valueAtConstruct;
	public String valueAtInject;
}
