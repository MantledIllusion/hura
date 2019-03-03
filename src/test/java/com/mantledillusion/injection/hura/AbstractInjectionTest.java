package com.mantledillusion.injection.hura;

import org.junit.Before;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.annotation.injection.Inject;
import com.mantledillusion.injection.hura.context.misc.ExampleContext;

public class AbstractInjectionTest {

	protected static final class InjectorTestSuite {
		
		@Inject
		private Injector injector;
		
		@Construct
		private InjectorTestSuite() {
		}
		
		public <T> T injectInRootContext(Class<T> type, Predefinable... predefinables) {
			return Injector.of(predefinables).instantiate(type);
		}
		
		public <T> T injectInSuiteContext(Class<T> type, Predefinable... predefinables) {
			return this.injector.instantiate(Blueprint.of(type, predefinables));
		}
		
		public <T> T injectInSuiteContext(TypedBlueprint<T> blueprint) {
			return this.injector.instantiate(blueprint);
		}
		
		public <T> T injectInSuiteContext(Class<T> type, ExampleContext context) {
			return this.injector.instantiate(Blueprint.of(type, Singleton.of(ExampleContext.SINGLETON, context)));
		}
		
		public void destroyInSuiteContext(Object wiredInstance) {
			this.injector.destroy(wiredInstance);
		}
	}
	
	protected InjectorTestSuite suite;
	
	@Before
	public void setup() {
		this.suite = Injector.of().instantiate(InjectorTestSuite.class);
	}

}
