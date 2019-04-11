package com.mantledillusion.injection.hura.core.utils;

import com.mantledillusion.injection.hura.core.InjectionUtils;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.core.property.injectables.InjectableWithResolvableConstructor;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InjectionUtilsTest {
	
	@Test
	public void testResolvability() throws NoSuchFieldException, SecurityException {
		assertTrue(InjectionUtils.isResolvable(InjectableWithProperty.class.getField("propertyValue")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullResolvability() {
		assertTrue(InjectionUtils.isResolvable(null));
	}
	
	@Test
	public void testInjectability() throws NoSuchFieldException, SecurityException {
		assertTrue(InjectionUtils.isInjectable(InjectableWithInjectableField.class.getField("wiredField")));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullInjectability() {
		assertTrue(InjectionUtils.isInjectable(null));
	}
	
	@Test
	public void testDefinability() {
		assertTrue(InjectionUtils.hasAllParametersDefinable(InjectableWithResolvableConstructor.class.getConstructors()[0]));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullDefinability() {
		assertTrue(InjectionUtils.hasAllParametersDefinable(null));
	}

	@Test
	public void testFindCollectionType() {
		Type type = TypeUtils.parameterize(Collection.class, String.class);
		assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(List.class, String.class);
		assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(Set.class, String.class);
		assertSame(String.class, InjectionUtils.findCollectionType(type));

		type = TypeUtils.parameterize(List.class, TypeUtils.wildcardType().build());
		assertSame(Object.class, InjectionUtils.findCollectionType(type));
	}
}
