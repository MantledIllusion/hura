package com.mantledillusion.injection.hura.core.injection.uninjectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

import java.io.Serializable;

public class UninjectableWithWiredInterfaceField {

	@Inject
	public Serializable wiredInterface;
}
