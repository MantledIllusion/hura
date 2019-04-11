package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;

public class InjectableWithDestructionAwareness {

	public static class InjectableWithDestructableSingleton {

		public static final String QUALIFIER = "qualifier";

		@Inject
		@Qualifier(QUALIFIER)
		public InjectableWithDestructionAwareness singleton;
	}

	public static class InjectableWithDestructableSingletonAndInjector {

		public static final String QUALIFIER = "qualifier";

		@Inject
		@Qualifier(QUALIFIER)
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
