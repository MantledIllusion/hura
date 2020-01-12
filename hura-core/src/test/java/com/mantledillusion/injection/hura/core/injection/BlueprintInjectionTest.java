package com.mantledillusion.injection.hura.core.injection;

import com.mantledillusion.injection.hura.core.AbstractInjectionTest;
import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injectable;
import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.instruction.Define;
import com.mantledillusion.injection.hura.core.exception.BlueprintException;
import com.mantledillusion.injection.hura.core.exception.ValidatorException;
import com.mantledillusion.injection.hura.core.injection.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.core.singleton.injectables.InjectableWithExplicitSingleton;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class BlueprintInjectionTest extends AbstractInjectionTest {

	@Test
	public void testNullBlueprintInjection() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.suite.injectInSuiteContext(null));
	}

	@Test
	public void testNullBlueprintCreation() {
		this.suite.injectInSuiteContext(Injectable.class, (Blueprint.Allocation) null, null);
		this.suite.injectInSuiteContext(Injectable.class, (Blueprint) null, null);
	}

	@Test
	public void testWrongAllocMethodReturnTypeBlueprintInjection() {
		Assertions.assertThrows(ValidatorException.class, () -> this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {

			@Define
			public String wrongReturnTypeAllocationMethod() {
				return StringUtils.EMPTY;
			}
		}));
	}

	@Test
	public void testParameteredAllocMethodBlueprintInjection() {
		Assertions.assertThrows(ValidatorException.class, () -> this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {

			@Define
			public Blueprint.TypeAllocation parameteredAllocationMethod(String param) {
				return null;
			}
		}));
	}
	
	@Test
	public void testNullAllocationBlueprintInjection() {
		Assertions.assertThrows(BlueprintException.class, () -> this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {

			@Define
			public Blueprint.TypeAllocation nullReturningAllocationMethod() {
				return null;
			}
		}));
	}

	@Test
	public void testNullTypeAllocationBlueprintInjection() {
		Assertions.assertThrows(BlueprintException.class, () -> this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {

			@Define
			public Blueprint.TypeAllocation nullTypeAllocationReturningMethod() {
				return Blueprint.TypeAllocation.allocateToType(Injectable.class, null);
			}
		}));
	}
	
	@Test
	public void testBlueprintInstanceSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonInstanceAllocationMethod() {
				return Blueprint.SingletonAllocation.allocateToInstance(InjectableWithExplicitSingleton.SINGLETON, instance);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintProviderSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonProviderAllocationMethod() {
				return Blueprint.SingletonAllocation.allocateToProvider(InjectableWithExplicitSingleton.SINGLETON, callback -> instance);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonTypeAllocationMethod() {
				return Blueprint.SingletonAllocation.allocateToType(InjectableWithExplicitSingleton.SINGLETON, Injectable.class);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {

			@Define
			public Blueprint.SingletonAllocation singletonTypeAllocationMethod() {
				return Blueprint.SingletonAllocation.allocateToPlugin(InjectableWithExplicitSingleton.SINGLETON, InjectableInterface.class, new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable != null);
	}
	
	@Test
	public void testBlueprintInstanceIndependentInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation instanceAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToInstance(InjectableInterface.class, instance);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintProviderIndependentInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation providerAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToProvider(InjectableInterface.class, callback -> instance);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation typeAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToType(InjectableInterface.class, Injectable.class);
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {

			@Define
			public Blueprint.TypeAllocation pluginAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToPlugin(InjectableInterface.class, new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		});

		Assertions.assertTrue(injectable.explicitInjectable != null);
	}
}
