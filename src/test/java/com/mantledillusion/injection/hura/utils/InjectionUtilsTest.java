package com.mantledillusion.injection.hura.utils;

import static org.junit.Assert.assertTrue;

import com.mantledillusion.injection.hura.InjectionUtils;
import org.junit.Test;

import com.mantledillusion.injection.hura.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithProperty;
import com.mantledillusion.injection.hura.property.injectables.InjectableWithResolvableConstructor;

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
}
