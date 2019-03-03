package com.mantledillusion.injection.hura.injection.uninjectables;

import java.io.Serializable;

import com.mantledillusion.injection.hura.annotation.injection.Inject;

public class UninjectableWithWiredInterfaceField {

	@Inject
	public Serializable wiredInterface;
}
