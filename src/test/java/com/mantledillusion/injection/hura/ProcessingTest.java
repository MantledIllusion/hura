package com.mantledillusion.injection.hura;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprintTemplate;
import com.mantledillusion.injection.hura.Injector.RootInjector;
import com.mantledillusion.injection.hura.Injector.TemporalInjectorCallback;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.annotation.Define;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import com.mantledillusion.injection.hura.exception.ValidatorException;
import com.mantledillusion.injection.hura.exception.BlueprintException;
import com.mantledillusion.injection.hura.injectables.InjectableWithDestructableSingleton;
import com.mantledillusion.injection.hura.injectables.InjectableWithDestructableSingletonAndInjector;
import com.mantledillusion.injection.hura.injectables.InjectableWithInjectableAndRelay;
import com.mantledillusion.injection.hura.injectables.InjectableWithInspectedAnnotations;
import com.mantledillusion.injection.hura.injectables.InjectableWithManualInjectionsDuringFinalizePhase;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessableFields;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessingAwareness;
import com.mantledillusion.injection.hura.injectables.InjectableWithProcessor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithFailingProcessor;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithManualInjectionsDuringInjectPhase;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithMultiParameterProcessMethod;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithStaticProcessMethod;
import com.mantledillusion.injection.hura.uninjectables.UninjectableWithWrongTypeParameterProcessMethod;

public class ProcessingTest extends AbstractInjectionTest {
	
	@Test
	public void testProcessingPhasesByCustomAnnotations() throws NoSuchFieldException, NoSuchMethodException, SecurityException {
		InjectableWithInspectedAnnotations injectable = this.suite.injectInSuiteContext(InjectableWithInspectedAnnotations.class);
		checkProcessable(injectable);
		
		assertEquals(InjectableWithInspectedAnnotations.class, injectable.elementAtInspect);
		assertEquals(InjectableWithInspectedAnnotations.class.getDeclaredField("inspectedField"), injectable.elementAtConstruct);
		assertEquals(InjectableWithInspectedAnnotations.class.getDeclaredMethod("inspectedMethod"), injectable.elementAtInject);
		assertEquals(InjectableWithInspectedAnnotations.class, injectable.elementAtFinalize);
		assertEquals(InjectableWithInspectedAnnotations.class, injectable.elementAtDestroy);
	}
	
	@Test
	public void testProcessingPhasesByClassAnnotation() {
		checkProcessable(this.suite.injectInSuiteContext(InjectableWithProcessor.class));
	}

	@Test
	public void testProcessingPhasesByMethodAnnotation() {
		checkProcessable(this.suite.injectInSuiteContext(InjectableWithProcessingAwareness.class));
	}

	@Test
	public void testProcessingPhasesByBlueprint() {
		Processor<InjectableWithProcessableFields> inspectPostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtInspect = bean.injectable;
				bean.occurredPhases.add(Phase.INSPECT);
			}
		};
		
		Processor<InjectableWithProcessableFields> constructPostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtConstruct = bean.injectable;
				bean.occurredPhases.add(Phase.CONSTRUCT);
			}
		};
		
		Processor<InjectableWithProcessableFields> injectPostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtInject = bean.injectable;
				bean.occurredPhases.add(Phase.INJECT);
			}
		};
		
		Processor<InjectableWithProcessableFields> finalizePostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtFinalize = bean.injectable;
				bean.occurredPhases.add(Phase.FINALIZE);
			}
		};
		
		Processor<InjectableWithProcessableFields> destroyPostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtDestroy = bean.injectable;
				bean.occurredPhases.add(Phase.DESTROY);
			}
		};
		
		InjectableWithProcessableFields injectable = this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithProcessableFields>() {

			@Override
			public Class<InjectableWithProcessableFields> getRootType() {
				return InjectableWithProcessableFields.class;
			}
			
			@Define
			private BeanAllocation<InjectableWithProcessableFields> allocate() {
				return BeanAllocation.allocateToType(InjectableWithProcessableFields.class, 
						PhasedProcessor.of(inspectPostProcessor, Phase.INSPECT), 
						PhasedProcessor.of(constructPostProcessor, Phase.CONSTRUCT), 
						PhasedProcessor.of(injectPostProcessor, Phase.INJECT), 
						PhasedProcessor.of(finalizePostProcessor, Phase.FINALIZE), 
						PhasedProcessor.of(destroyPostProcessor, Phase.DESTROY));
			}
			
		}));
		
		checkProcessable(injectable);
	}
	
	private void checkProcessable(InjectableWithProcessableFields postProcessable) {
		this.suite.destroyInSuiteContext(postProcessable);
		assertTrue(postProcessable.injectableAtInspect == null);
		assertTrue(postProcessable.injectableAtConstruct == null);
		assertTrue(postProcessable.injectableAtInject == postProcessable.injectable);
		assertTrue(postProcessable.injectableAtFinalize == postProcessable.injectable);
		assertTrue(postProcessable.injectableAtDestroy == postProcessable.injectable);
		assertEquals(Arrays.asList(Phase.INSPECT, Phase.CONSTRUCT, Phase.INJECT, Phase.FINALIZE, Phase.DESTROY), postProcessable.occurredPhases);
	}
	
	@Test(expected=ValidatorException.class)
	public void testStaticPostProcessMethodInjection() {
		this.suite.injectInSuiteContext(UninjectableWithStaticProcessMethod.class);
	}
	
	@Test(expected=ValidatorException.class)
	public void testMultiParameteredPostProcessMethodInjection() {
		this.suite.injectInSuiteContext(UninjectableWithMultiParameterProcessMethod.class);
	}
	
	@Test(expected=ValidatorException.class)
	public void testWrongParameterTypePostProcessMethodInjection() {
		this.suite.injectInSuiteContext(UninjectableWithWrongTypeParameterProcessMethod.class);
	}

	@Test(expected=BlueprintException.class)
	public void testBlueprintPostProcessingWithoutPostProcessor() {
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithProcessableFields>() {

			@Override
			public Class<InjectableWithProcessableFields> getRootType() {
				return InjectableWithProcessableFields.class;
			}
			
			@Define
			private BeanAllocation<InjectableWithProcessableFields> allocate() {
				return BeanAllocation.allocateToType(InjectableWithProcessableFields.class, 
						PhasedProcessor.of(null, Phase.CONSTRUCT));
			}
			
		}));
	}

	@Test(expected=BlueprintException.class)
	public void testBlueprintPostProcessingWithoutPhase() {
		Processor<InjectableWithProcessableFields> constructPostProcessor = new Processor<InjectableWithProcessableFields>() {

			@Override
			public void process(InjectableWithProcessableFields bean, TemporalInjectorCallback callback) throws Exception {
				bean.injectableAtConstruct = bean.injectable;
			}
		};
		
		this.suite.injectInSuiteContext(TypedBlueprint.from(new TypedBlueprintTemplate<InjectableWithProcessableFields>() {

			@Override
			public Class<InjectableWithProcessableFields> getRootType() {
				return InjectableWithProcessableFields.class;
			}
			
			@Define
			private BeanAllocation<InjectableWithProcessableFields> allocate() {
				return BeanAllocation.allocateToType(InjectableWithProcessableFields.class, 
						PhasedProcessor.of(constructPostProcessor, null));
			}
			
		}));
	}
	
	@Test(expected=ProcessorException.class)
	public void testFailingPostProcessing() {
		this.suite.injectInSuiteContext(UninjectableWithFailingProcessor.class);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnknownInstanceDestruction() {
		this.suite.destroyInSuiteContext(Boolean.TRUE);
	}
	
	@Test
	public void testFinalizingOrder() {
		InjectableWithInjectableAndRelay injectable = this.suite.injectInSuiteContext(InjectableWithInjectableAndRelay.class);
		assertTrue(injectable.injectable.hasRun);
	}
	
	@Test
	public void testSingletonDestruction() {
		RootInjector rootInjector = Injector.of();
		
		InjectableWithDestructableSingletonAndInjector injectable = rootInjector.instantiate(InjectableWithDestructableSingletonAndInjector.class);
		InjectableWithDestructableSingleton sub = injectable.injector.instantiate(InjectableWithDestructableSingleton.class);

		assertFalse(injectable.singleton.wasDestructed);
		injectable.injector.destroy(sub);
		assertFalse(injectable.singleton.wasDestructed);
		rootInjector.destroy(injectable);
		assertTrue(injectable.singleton.wasDestructed);
	}
	
	@Test(expected=ProcessorException.class)
	public void testManualInjectionDuringInjectProcessing() {
		this.suite.injectInSuiteContext(UninjectableWithManualInjectionsDuringInjectPhase.class);
	}
	
	@Test 
	public void testManualInjectionDuringFinalizeProcessing() {
		this.suite.injectInSuiteContext(InjectableWithManualInjectionsDuringFinalizePhase.class);
	}
}
