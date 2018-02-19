package com.mantledillusion.injection.hura.uninjectables;
import com.mantledillusion.injection.hura.annotation.Process;

public class UninjectableWithWrongTypeParameterProcessMethod {

	@Process
	private void postProcess(String param) {
		
	}
}
