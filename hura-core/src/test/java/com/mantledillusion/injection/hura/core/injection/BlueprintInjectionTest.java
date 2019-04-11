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
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class BlueprintInjectionTest extends AbstractInjectionTest {

	@Test(expected=IllegalArgumentException.class)
	public void testNullBlueprintInjection() {
		this.suite.injectInSuiteContext(null);
	}

	@Test
	public void testNullBlueprintCreation() {
		this.suite.injectInSuiteContext(Injectable.class, (Blueprint.Allocation) null, null);
		this.suite.injectInSuiteContext(Injectable.class, (Blueprint) null, null);
	}

	@Test(expected= ValidatorException.class)
	public void testWrongAllocMethodReturnTypeBlueprintInjection() {
		this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {
			
			@Define
			public String wrongReturnTypeAllocationMethod() {
				return StringUtils.EMPTY;
			}
		});
	}

	@Test(expected=ValidatorException.class)
	public void testParameteredAllocMethodBlueprintInjection() {
		this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation parameteredAllocationMethod(String param) {
				return null;
			}
		});
	}
	
	@Test(expected= BlueprintException.class)
	public void testNullAllocationBlueprintInjection() {
		this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation nullReturningAllocationMethod() {
				return null;
			}
		});
	}

	@Test(expected=BlueprintException.class)
	public void testNullTypeAllocationBlueprintInjection() {
		this.suite.injectInSuiteContext(Injectable.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation nullTypeAllocationReturningMethod() {
				return Blueprint.TypeAllocation.allocateToType(Injectable.class, null);
			}
		});
	}
	
	@Test
	public void testBlueprintInstanceSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonInstanceAllocationMethod() {
				return Blueprint.SingletonAllocation.of(InjectableWithExplicitSingleton.SINGLETON, instance);
			}
		});
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintProviderSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonProviderAllocationMethod() {
				return Blueprint.SingletonAllocation.of(InjectableWithExplicitSingleton.SINGLETON, callback -> instance);
			}
		});
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {
			
			@Define
			public Blueprint.SingletonAllocation singletonTypeAllocationMethod() {
				return Blueprint.SingletonAllocation.of(InjectableWithExplicitSingleton.SINGLETON, Injectable.class);
			}
		});
		
		assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(InjectableWithExplicitSingleton.class, new Blueprint() {

			@Define
			public Blueprint.SingletonAllocation singletonTypeAllocationMethod() {
				return Blueprint.SingletonAllocation.of(InjectableWithExplicitSingleton.SINGLETON, InjectableInterface.class, new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		});

		assertTrue(injectable.explicitInjectable != null);
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
		
		assertTrue(injectable.explicitInjectable == instance);
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
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {
			
			@Define
			public Blueprint.TypeAllocation typeAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToType(InjectableInterface.class, Injectable.class);
			}
		});
		
		assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(InjectableWithExplicitIndependent.class, new Blueprint() {

			@Define
			public Blueprint.TypeAllocation pluginAllocationMethod() {
				return Blueprint.TypeAllocation.allocateToPlugin(InjectableInterface.class, new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		});

		assertTrue(injectable.explicitInjectable != null);
	}
}
