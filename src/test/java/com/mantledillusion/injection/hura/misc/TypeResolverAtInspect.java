package com.mantledillusion.injection.hura.misc;

import com.mantledillusion.injection.hura.Inspector;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectingResolver;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;

public class TypeResolverAtInspect implements Inspector<ResolvingTypeAnnotationAtInspect, Class<?>> {

	@Override
	public void inspect(Object bean, ResolvingTypeAnnotationAtInspect annotationInstance, Class<?> annotatedElement,
			TemporalInjectorCallback callback) throws Exception {
		((InjectableWithInspectingResolver) bean).propertyValue = callback.resolve("property.key", "someDefaultValueThatWillNotBeUsed");
	}
}
