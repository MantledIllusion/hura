package com.mantledillusion.injection.hura;

import org.junit.Before;

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

		public <T> T injectInRootContext(Class<T> type) {
			return Injector.of().instantiate(type);
		}
		
		public <T> T injectInRootContext(Class<T> type, Blueprint.Allocation allocation, Blueprint.Allocation... allocations) {
			return Injector.of(allocation, allocations).instantiate(type);
		}

		public <T> T injectInSuiteContext(Class<T> type) {
			return this.injector.instantiate(type);
		}
		
		public <T> T injectInSuiteContext(Class<T> type, Blueprint.Allocation allocation, Blueprint.Allocation... allocations) {
			return this.injector.instantiate(type, allocation, allocations);
		}
		
		public <T> T injectInSuiteContext(Class<T> type, Blueprint blueprint, Blueprint... blueprints) {
			return this.injector.instantiate(type, blueprint, blueprints);
		}
		
		public <T> T injectInSuiteContext(Class<T> type, ExampleContext context) {
			return this.injector.instantiate(type, Blueprint.SingletonAllocation.of(ExampleContext.SINGLETON, context));
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
