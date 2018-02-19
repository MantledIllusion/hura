package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjectedPropertyField;

public class TypeInjectionResolvingProcessorAtInject implements Processor<InjectableWithInjectedPropertyField> {

	@Override
	public void process(InjectableWithInjectedPropertyField bean, TemporalInjectorCallback callback) throws Exception {
		bean.valueAtInject = bean.injectableProperty;
	}
}
