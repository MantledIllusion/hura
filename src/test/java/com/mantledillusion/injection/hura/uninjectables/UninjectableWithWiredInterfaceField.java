package com.mantledillusion.injection.hura.uninjectables;

import java.io.Serializable;

import com.mantledillusion.injection.hura.annotation.Inject;

public class UninjectableWithWiredInterfaceField {

	@Inject
	public Serializable wiredInterface;
}
