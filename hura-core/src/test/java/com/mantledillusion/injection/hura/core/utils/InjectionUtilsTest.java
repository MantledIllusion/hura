package com.mantledillusion.injection.hura.core.utils;

import com.mantledillusion.injection.hura.core.InjectionUtils;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithResolvableConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class InjectionUtilsTest {
	
	@Test
	public void testResolvability() throws NoSuchFieldException, SecurityException {
		Assertions.assertTrue(InjectionUtils.isResolvable(InjectableWithProperty.class.getField("propertyValue")));
	}
	
	@Test
	public void testNullResolvability() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> InjectionUtils.isResolvable(null));
	}
	
	@Test
	public void testInjectability() throws NoSuchFieldException, SecurityException {
		Assertions.assertTrue(InjectionUtils.isInjectable(InjectableWithInjectableField.class.getField("wiredField")));
	}
	
	@Test
	public void testNullInjectability() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> InjectionUtils.isInjectable(null));
	}
	
	@Test
	public void testDefinability() {
		Assertions.assertTrue(InjectionUtils.hasAllParametersDefinable(InjectableWithResolvableConstructor.class.getConstructors()[0]));
	}
	
	@Test
	public void testNullDefinability() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> InjectionUtils.hasAllParametersDefinable(null));
	}

	@Test
	public void testFindCollectionType() {
		Type type = TypeUtils.parameterize(Collection.class, String.class);
		Assertions.assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(List.class, String.class);
		Assertions.assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(Set.class, String.class);
		Assertions.assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(List.class, TypeUtils.wildcardType().build());
		Assertions.assertSame(Object.class, InjectionUtils.findCollectionType(type));
	}
}
