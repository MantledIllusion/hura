package com.mantledillusion.injection.hura.injection;

import static org.junit.Assert.assertTrue;

import java.io.File;

import com.mantledillusion.injection.hura.*;
import com.mantledillusion.injection.hura.injection.injectables.InjectableWithExplicitIndependent;
import com.mantledillusion.injection.hura.injection.injectables.InjectableWithInjectableField;
import com.mantledillusion.injection.hura.singleton.injectables.InjectableWithExplicitSingleton;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprintTemplate;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.annotation.instruction.Define;
import com.mantledillusion.injection.hura.exception.BlueprintException;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import com.mantledillusion.injection.hura.InjectableInterface;

public class BlueprintInjectionTest extends AbstractInjectionTest {

	@Test(expected=IllegalArgumentException.class)
	public void testNullBlueprintInjection() {
		this.suite.injectInSuiteContext((TypedBlueprint<?>) null);
	}
	
	@Test
	public void testBasicBlueprintInjection() {
		InjectableWithInjectableField injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(() -> InjectableWithInjectableField.class));
		
		assertTrue(injectable.wiredField != null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNullBlueprintCreation() {
		TypedBlueprint.from((BlueprintTemplate) null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullTypedBlueprintCreation() {
		TypedBlueprint.from((TypedBlueprintTemplate<?>) null);
	}
	
	@Test(expected=BlueprintException.class)
	public void testNullReturningBlueprintInjection() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(() -> null));
	}

	@Test(expected=ValidatorException.class)
	public void testWrongAllocMethodReturnTypeBlueprintInjection() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<Injectable>() {

			@Override
			public Class<Injectable> getRootType() {
				return Injectable.class;
			}
			
			@Define
			public String wrongReturnTypeAllocationMethod() {
				return StringUtils.EMPTY;
			}
		}));
	}

	@Test(expected=ValidatorException.class)
	public void testParameteredAllocMethodBlueprintInjection() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<Injectable>() {

			@Override
			public Class<Injectable> getRootType() {
				return Injectable.class;
			}
			
			@Define
			public BeanAllocation<String> parameteredAllocationMethod(String param) {
				return null;
			}
		}));
	}
	
	@Test(expected=BlueprintException.class)
	public void testNullAllocationBlueprintInjection() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<Injectable>() {

			@Override
			public Class<Injectable> getRootType() {
				return Injectable.class;
			}
			
			@Define
			public BeanAllocation<String> nullReturningAllocationMethod() {
				return null;
			}
		}));
	}
	
	@Test(expected=BlueprintException.class)
	public void testNullTypeAllocationBlueprintInjection() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<Injectable>() {

			@Override
			public Class<Injectable> getRootType() {
				return Injectable.class;
			}
			
			@Define
			public BeanAllocation<String> nullTypeAllocationReturningMethod() {
				return BeanAllocation.allocateToType(null);
			}
		}));
	}
	
	@Test
	public void testBlueprintInstanceSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitSingleton>() {

			@Override
			public Class<InjectableWithExplicitSingleton> getRootType() {
				return InjectableWithExplicitSingleton.class;
			}
			
			@Define
			public Singleton singletonInstanceAllocationMethod() {
				return Singleton.of(InjectableWithExplicitSingleton.SINGLETON, instance);
			}
		}));
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintProviderSingletonInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitSingleton>() {

			@Override
			public Class<InjectableWithExplicitSingleton> getRootType() {
				return InjectableWithExplicitSingleton.class;
			}
			
			@Define
			public Singleton singletonProviderAllocationMethod() {
				return Singleton.of(InjectableWithExplicitSingleton.SINGLETON, callback -> instance);
			}
		}));
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitSingleton>() {

			@Override
			public Class<InjectableWithExplicitSingleton> getRootType() {
				return InjectableWithExplicitSingleton.class;
			}
			
			@Define
			public Singleton singletonTypeAllocationMethod() {
				return Singleton.of(InjectableWithExplicitSingleton.SINGLETON, Injectable.class);
			}
		}));
		
		assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginSingletonInjection() {
		InjectableWithExplicitSingleton injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitSingleton>() {

			@Override
			public Class<InjectableWithExplicitSingleton> getRootType() {
				return InjectableWithExplicitSingleton.class;
			}

			@Define
			public Singleton singletonTypeAllocationMethod() {
				return Singleton.of(InjectableWithExplicitSingleton.SINGLETON, InjectableInterface.class, new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		}));

		assertTrue(injectable.explicitInjectable != null);
	}
	
	@Test
	public void testBlueprintInstanceIndependentInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitIndependent>() {

			@Override
			public Class<InjectableWithExplicitIndependent> getRootType() {
				return InjectableWithExplicitIndependent.class;
			}
			
			@Define
			public BeanAllocation<InjectableInterface> instanceAllocationMethod() {
				return BeanAllocation.allocateToInstance(instance);
			}
		}));
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintProviderIndependentInjection() {
		Injectable instance = new Injectable();
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitIndependent>() {

			@Override
			public Class<InjectableWithExplicitIndependent> getRootType() {
				return InjectableWithExplicitIndependent.class;
			}
			
			@Define
			public BeanAllocation<InjectableInterface> providerAllocationMethod() {
				return BeanAllocation.allocateToProvider(callback -> instance);
			}
		}));
		
		assertTrue(injectable.explicitInjectable == instance);
	}
	
	@Test
	public void testBlueprintTypeIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitIndependent>() {

			@Override
			public Class<InjectableWithExplicitIndependent> getRootType() {
				return InjectableWithExplicitIndependent.class;
			}
			
			@Define
			public BeanAllocation<InjectableInterface> typeAllocationMethod() {
				return BeanAllocation.allocateToType(Injectable.class);
			}
		}));
		
		assertTrue(injectable.explicitInjectable != null);
	}

	@Test
	public void testBlueprintPluginIndependentInjection() {
		InjectableWithExplicitIndependent injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithExplicitIndependent>() {

			@Override
			public Class<InjectableWithExplicitIndependent> getRootType() {
				return InjectableWithExplicitIndependent.class;
			}

			@Define
			public BeanAllocation<InjectableInterface> pluginAllocationMethod() {
				return BeanAllocation.allocateToPlugin(new File("src/test/resources/plugins"), "InjectableInterfacePlugin");
			}
		}));

		assertTrue(injectable.explicitInjectable != null);
	}
}
