package com.mantledillusion.injection.hura.lifecycle.injectables;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.annotation.lifecycle.bean.PreDestroy;

public class InjectableWithDestructionAwareness {

	public static class InjectableWithDestructableSingleton {

		public static final String QUALIFIER = "qualifier";

		@Inject(QUALIFIER)
		public InjectableWithDestructionAwareness singleton;
	}

	public static class InjectableWithDestructableSingletonAndInjector {

		public static final String QUALIFIER = "qualifier";

		@Inject(QUALIFIER)
		public InjectableWithDestructionAwareness singleton;
		@Inject
		public Injector injector;
	}

	public boolean wasDestructed = false;

	@PreDestroy
	private void destruct() {
		this.wasDestructed = true;
	}
}
