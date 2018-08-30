package com.mantledillusion.injection.hura;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.BeanAllocation.BeanProvider;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.InjectionContext.GlobalInjectionContext;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.Predefinable.Mapping;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableConstructor;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableConstructor.ParamSettingType;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableField;
import com.mantledillusion.injection.hura.ReflectionCache.ResolvableField;
import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Context;
import com.mantledillusion.injection.hura.annotation.Global;
import com.mantledillusion.injection.hura.annotation.Global.SingletonMode;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Optional.InjectionMode;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import com.mantledillusion.injection.hura.exception.InjectionException;

/**
 * An {@link Injector} for instantiating and injecting beans.
 * <p>
 * The root {@link Injector} of an injection tree may be instantiated manually
 * using {@link #of(Predefinable...)}, which returns a specific sub type of
 * {@link Injector}; the {@link RootInjector}. All other {@link Injector}
 * instances down the tree (that are needed in beans created by an
 * {@link Injector} of any kind) should not instantiated manually, but injected
 * by their respective parent {@link Injector}.
 * <p>
 * With every injection sequence, an {@link Injector} returns exactly one bean.
 * The bean itself is instantiated by the {@link Injector} and may contain sub
 * beans that get injected in the process. This root bean referencing injected
 * sub beans is the root of an injection sub tree and can be given to
 * {@link #destroy(Object)} for sub tree destruction.
 * <p>
 * A sub bean is injected by using the @{@link Inject} annotation and is either
 * instantiated itself and/or referenced in case it is a {@link Singleton}.
 * <p>
 * When an {@link Injector} injects another {@link Injector}, it passes its own
 * {@link InjectionContext} onto this child, making it possible that to pass
 * {@link Singleton} beans further down the injection tree, but not up. As a
 * result, injections by child {@link Injector}s are able to retrieve
 * {@link Singleton}s from the same pool as their parent in the tree, but cannot
 * define {@link Singleton}s that will be available to the parent (or another
 * one of its children).
 */
public class Injector extends InjectionProvider {

	/**
	 * The root {@link Injector} of an injection tree.
	 * <p>
	 * This specific {@link Injector} type defines and holds the
	 * {@link SingletonMode#GLOBAL} {@link InjectionContext} that contains those
	 * {@link Singleton}s that need to be available to the whole injection tree.
	 */
	public static final class RootInjector extends Injector {

		private RootInjector(GlobalInjectionContext globalInjectionContext, ResolvingContext resolvingContext,
				MappingContext mappingContext, TypeContext typeContext) {
			super(globalInjectionContext, resolvingContext, mappingContext, typeContext);
		}

		/**
		 * Extension to {@link #destroyAll()} that not only destroys the whole injection
		 * tree, but also all {@link SingletonMode#GLOBAL} {@link Singleton}s.
		 */
		public void destroyInjector() {
			destroyAll();
			((Injector) this).globalInjectionContext.destroy();
		}

		/**
		 * Normal {@link Injector#destroyAll()} method, but since it is called on the
		 * {@link RootInjector}, it destroyes the whole injection tree.
		 */
		@Override
		public void destroyAll() {
			super.destroyAll();
		}
	}

	/**
	 * A temporarily valid injection callback that can be used to instantiate a bean
	 * during a specific processing of a currently running injection sequence of an
	 * {@link Injector}.
	 * <p>
	 * Since subsequent injection sequences cannot be started during the processing
	 * of a parent injection sequence, this callback can be used during processing
	 * to perform a manually triggered injection in the currently running sequence.
	 * For example, it is prohibited to call an {@link Injector} to inject something
	 * during a @{@link Process} method call. Doing so would create the possibility
	 * of incoherent {@link Singleton} pools, since the currently running injection
	 * sequence (which has caused the @{@link Process} method to be called) could
	 * define a sequence singleton later on that such an intermediate injection
	 * sequence created and finished during the method would not be able to obtain.
	 * <p>
	 * The callback automatically looses its instantiation ability when the
	 * injection sequence proceeds; any successive calls on the callback's functions
	 * will result in {@link IllegalStateException}s. The current state can be
	 * checked with {@link TemporalInjectorCallback#isActive()}.
	 */
	public final class TemporalInjectorCallback extends InjectionProvider {

		private final InjectionChain chain;
		private boolean isActive = true;

		private TemporalInjectorCallback(InjectionChain chain) {
			this.chain = chain;
		}

		/**
		 * Determines whether this {@link TemporalInjectorCallback} is currently active.
		 * <p>
		 * In other words, this method returns whether the injection sequence this
		 * callback has been created for is still running, so the callback is able to
		 * instantiate more beans in that sequence's context.
		 * 
		 * @return True if the sequence is still active, false otherwise; if false is
		 *         returned, all calls to any of the
		 *         {@link #instantiate(Class, Predefinable...)} or
		 *         {@link #resolve(String)} {@link Method}s will fail with
		 *         {@link IllegalStateException}s
		 */
		public boolean isActive() {
			return this.isActive;
		}

		private void deactivate() {
			this.isActive = false;
		}

		@Override
		String resolve(ResolvingSettings set) {
			checkActive();

			return this.chain.resolve(set);
		}

		@Override
		public <T> T instantiate(TypedBlueprint<T> blueprint) {
			checkActive();

			InjectionChain chain = this.chain.extendBy(blueprint);
			InjectionSettings<T> set = InjectionSettings.of(blueprint);
			return Injector.this.instantiate(chain, set);
		}

		private void checkActive() {
			if (!this.isActive) {
				throw new IllegalStateException(
						"The temporally restricted lifetime of the injection callback has expired.");
			}
		}
	}

	static abstract class AbstractAllocator<T> {

		private AbstractAllocator() {
		}

		abstract T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
				InjectionProcessors<T> applicators);
	}

	static final class InstanceAllocator<T> extends AbstractAllocator<T> {

		private final T instance;

		InstanceAllocator(T instance) {
			this.instance = instance;
		}

		@Override
		T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
				InjectionProcessors<T> applicators) {
			injector.handleDestroying(injectionChain, set, this.instance, Collections.emptyList(), true);
			return this.instance;
		}
	}

	static final class ProviderAllocator<T, T2 extends T> extends AbstractAllocator<T> {

		private final BeanProvider<T2> provider;

		ProviderAllocator(BeanProvider<T2> provider) {
			this.provider = provider;
		}

		@Override
		T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
				InjectionProcessors<T> applicators) {
			T bean = this.provider.provide(injector.new TemporalInjectorCallback(injectionChain));
			injector.handleDestroying(injectionChain, set, bean, Collections.emptyList(), true);
			return bean;
		}
	}

	static final class ClassAllocator<T, T2 extends T> extends AbstractAllocator<T> {

		private final Class<T2> clazz;
		private final InjectionProcessors<T2> applicators;

		ClassAllocator(Class<T2> clazz, InjectionProcessors<T2> applicators) {
			this.clazz = clazz;
			this.applicators = applicators;
		}

		@Override
		T allocate(Injector injector, InjectionChain chain, InjectionSettings<T> set,
				InjectionProcessors<T> applicators) {
			InjectionSettings<T2> refinedSettings = set.refine(this.clazz);
			InjectionProcessors<T2> refinedApplicators = injector.buildApplicators(chain, refinedSettings);
			return injector.createAndInject(chain, refinedSettings, this.applicators.merge(refinedApplicators), true);
		}
	}

	interface SelfSustaningProcessor {

		void process() throws Exception;
	}

	private final GlobalInjectionContext globalInjectionContext;
	private final InjectionContext baseInjectionContext;
	private final ResolvingContext resolvingContext;
	private final MappingContext mappingContext;
	private final TypeContext typeContext;

	private final IdentityHashMap<Object, List<SelfSustaningProcessor>> beans = new IdentityHashMap<>();

	@Construct
	private Injector(
			@Inject(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID) @Global GlobalInjectionContext globalInjectionContext,
			@Inject(InjectionContext.INJECTION_CONTEXT_SINGLETON_ID) InjectionContext baseInjectionContext,
			@Inject(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID) ResolvingContext resolvingContext,
			@Inject(MappingContext.MAPPING_CONTEXT_SINGLETON_ID) MappingContext mappingContext,
			@Inject(TypeContext.TYPE_CONTEXT_SINGLETON_ID) TypeContext typeContext) {
		this.globalInjectionContext = globalInjectionContext;
		this.baseInjectionContext = baseInjectionContext;
		this.resolvingContext = resolvingContext;
		this.mappingContext = mappingContext;
		this.typeContext = typeContext;
	}

	private Injector(GlobalInjectionContext globalInjectionContext, ResolvingContext resolvingContext,
			MappingContext mappingContext, TypeContext typeContext) {
		this.globalInjectionContext = globalInjectionContext;
		this.baseInjectionContext = new InjectionContext(globalInjectionContext.getInjectionTreeLock(),
				resolvingContext, mappingContext, typeContext);
		this.resolvingContext = resolvingContext;
		this.mappingContext = mappingContext;
		this.typeContext = typeContext;
	}

	@Override
	String resolve(ResolvingSettings set) {
		return this.resolvingContext.resolve(set);
	}

	@Override
	public final <T> T instantiate(TypedBlueprint<T> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}

		InjectionSettings<T> settings = InjectionSettings.of(blueprint);

		ResolvingContext resolvingContext = new ResolvingContext(this.resolvingContext)
				.merge(blueprint.getPropertyAllocations());
		MappingContext mappingContext = new MappingContext(this.mappingContext)
				.merge(blueprint.getMappingAllocations());
		TypeContext typeContext = new TypeContext(this.typeContext)
				.merge(blueprint.getTypeAllocations());

		InjectionContext injectionContext = new InjectionContext(this.globalInjectionContext.getInjectionTreeLock(),
				this.baseInjectionContext, resolvingContext, mappingContext, typeContext);

		InjectionChain chain = InjectionChain.forInjection(injectionContext, resolvingContext, mappingContext,
				typeContext, blueprint.getSingletonAllocations());

		T instance;
		try {
			resolveSingletonsIntoChain(chain, blueprint.getSingletonAllocations().keySet(), SingletonMode.SEQUENCE);

			instance = instantiate(chain, settings);
			this.beans.put(instance, chain.getDestroyables());
			finalize(chain.getFinalizables());
		} catch (Exception e) {
			chain.clearHook();

			int failingDestructionCount = 0;
			for (SelfSustaningProcessor destroyable : chain.getDestroyables()) {
				try {
					destroyable.process();
				} catch (Exception e1) {
					failingDestructionCount++;
					// Do nothing; If proper injection has failed, a failing destroying might just
					// be a consequence.
				}
			}

			if (failingDestructionCount > 0) {
				throw new InjectionException("Injection failed; " + chain.getDestroyables().size()
						+ " destructions were executed on already injected beans (with " + failingDestructionCount
						+ " of them failing as well)", e);
			} else {
				throw e;
			}
		}
		return instance;

	}

	private void finalize(List<SelfSustaningProcessor> finalizables) {
		Collections.reverse(finalizables);
		for (SelfSustaningProcessor finalizable : finalizables) {
			finalize(finalizable);
		}
	}

	private void finalize(SelfSustaningProcessor finalizable) {
		try {
			finalizable.process();
		} catch (Exception e) {
			throw new ProcessorException("Unable to finalize; the processing threw an exception", e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T instantiate(InjectionChain injectionChain, InjectionSettings<T> set) {
		InjectionChain hook = injectionChain;
		hook.hookOnThread();

		if (set.isContext && injectionChain.isChildOfGlobalSingleton()) {
			throw new InjectionException("Cannot refer to the type '" + set.type.getSimpleName()
					+ "' as it (or one of its super classes/interfaces) is marked with the "
					+ Context.class.getSimpleName()
					+ " annotation; context instances cannot be a sub bean of global singletons, but it is at "
					+ injectionChain.getStringifiedChainSinceDependency());
		}

		injectionChain = applyExtensions(injectionChain, set);

		T instance = null;

		if (set.isIndependent) {
			if (injectionChain.hasTypeAllocator(set.type)) {
				instance = ((AbstractAllocator<T>) injectionChain.getTypeAllocator(set.type)).allocate(this,
						injectionChain, set, buildApplicators(injectionChain, set));
			} else if (set.injectionMode == InjectionMode.EAGER) {
				instance = createAndInject(injectionChain, set, buildApplicators(injectionChain, set), false);
			}
		} else {
			Object singleton = null;

			if (injectionChain.hasMapping(set.qualifier, set.singletonMode)) {
				set = set.refine(injectionChain.map(set.qualifier, set.singletonMode));
			}

			boolean allocatedOnly = set.injectionMode == InjectionMode.EXPLICIT;
			if (set.singletonMode == SingletonMode.GLOBAL
					&& this.globalInjectionContext.hasSingleton(set.qualifier, set.type, allocatedOnly)) {
				singleton = this.globalInjectionContext.retrieveSingleton(set.qualifier);
			} else if (set.singletonMode == SingletonMode.GLOBAL
					&& injectionChain.hasGlobalSingletonAllocator(set.qualifier)) {
				singleton = ((AbstractAllocator<T>) injectionChain.getGlobalSingletonAllocator(set.qualifier))
						.allocate(this, injectionChain, set, buildApplicators(injectionChain, set));
			} else if (set.singletonMode == SingletonMode.SEQUENCE
					&& injectionChain.hasSingleton(set.qualifier, set.type, allocatedOnly)) {
				singleton = injectionChain.retrieveSingleton(set.qualifier);
			} else if (set.singletonMode == SingletonMode.SEQUENCE
					&& injectionChain.hasSequenceSingletonAllocator(set.qualifier)) {
				singleton = ((AbstractAllocator<T>) injectionChain.getSequenceSingletonAllocator(set.qualifier))
						.allocate(this, injectionChain, set, buildApplicators(injectionChain, set));
			} else if (!allocatedOnly) {
				singleton = createAndInject(injectionChain, set, buildApplicators(injectionChain, set), false);
			}

			if (singleton != null) {
				if (TypeUtils.isAssignable(singleton.getClass(), set.type)) {
					instance = (T) singleton;
				} else {
					throw new InjectionException("The singleton with the id '" + set.qualifier
							+ "' needs to be injected with an assignable of the type " + set.type.getSimpleName()
							+ ", but there already exists a singleton of that name with the type "
							+ singleton.getClass().getSimpleName() + " who is not assignable.");
				}
			}
		}

		hook.unhookFromThread();

		return instance;
	}

	private InjectionChain applyExtensions(InjectionChain chain, InjectionSettings<?> set) {
		List<Blueprint> parsed = new ArrayList<>();
		for (Class<? extends BlueprintTemplate> extension : set.extensions) {
			if (extension != null) {
				BlueprintTemplate instantiated = instantiate(chain, InjectionSettings.of(Blueprint.of(extension)));
				Blueprint blueprint = Blueprint.from(instantiated);
				if (!blueprint.getSingletonAllocations().isEmpty()) {
					throw new InjectionException("The blueprint template implementation '" + extension.getSimpleName()
							+ "' was used as an extension but defines " + blueprint.getSingletonAllocations().size()
							+ " singletons with the qualifiers ["
							+ StringUtils.join(blueprint.getSingletonAllocations().keySet(), ", ")
							+ "]; extensions may not define singletons, as that could cause singletons "
							+ "to be defined differently depending on where they are injected first.");
				}
				parsed.add(blueprint);
			}
		}
		parsed.add(set.predefinitions);
		return chain.adjustBy(parsed);
	}

	private <T> InjectionProcessors<T> buildApplicators(InjectionChain chain, InjectionSettings<T> set) {
		ReflectionCache.validate(set.type);
		TemporalInjectorCallback callback = new TemporalInjectorCallback(chain);
		InjectionProcessors<T> applicators = InjectionProcessors.of(set.type, callback);
		callback.deactivate();
		return applicators;
	}

	private <T> T createAndInject(InjectionChain injectionChain, InjectionSettings<T> set,
			InjectionProcessors<T> applicators, boolean isAllocatedInjection) {
		if (set.isContext) {
			throw new InjectionException("Cannot instantiate the type '" + set.type.getSimpleName()
					+ "' as it (or one of its super classes/interfaces) is marked with the "
					+ Context.class.getSimpleName() + " annotation; context instances have to be provided by the "
					+ TypedBlueprint.class.getSimpleName());
		} else if (!set.isIndependent && Injector.class.isAssignableFrom(set.type)) {
			throw new InjectionException("Cannot inject an " + Injector.class.getSimpleName()
					+ " as any kind of singleton, as that would allow taking their sequence context out of itself.");
		}

		InjectableConstructor<T> injectableConstructor = ReflectionCache.getInjectableConstructor(set.type);

		if (injectionChain.containsConstructor(injectableConstructor.getConstructor())) {
			throw new InjectionException("Injection dependecy cycle detected: "
					+ injectionChain.getStringifiedChainSinceConstructor(injectableConstructor.getConstructor()));
		}

		if (!set.isIndependent && set.singletonMode == SingletonMode.GLOBAL) {
			injectionChain = InjectionChain.forGlobalSingletonResolving(this.globalInjectionContext, injectionChain);
		} else {
			injectionChain = injectionChain.extendBy(injectableConstructor.getConstructor(), set);
		}

		Object[] parameters = new Object[injectableConstructor.getParamCount()];
		for (int i = 0; i < injectableConstructor.getParamCount(); i++) {
			ParamSettingType type = injectableConstructor.getSettingTypeOfParam(i);
			if (type == ParamSettingType.RESOLVABLE || type == ParamSettingType.BOTH) {
				parameters[i] = injectionChain.resolve(injectableConstructor.getResolvingSettings(i));
			}
			if (type == ParamSettingType.INJECTABLE || type == ParamSettingType.BOTH) {
				InjectionSettings<?> paramInjectionSettings = injectableConstructor.getInjectionSettings(i);
				Object instantiated = instantiate(injectionChain, paramInjectionSettings);
				parameters[i] = parameters[i] == null ? instantiated
						: (instantiated != null || paramInjectionSettings.overwriteWithNull ? instantiated
								: parameters[i]);
			}
		}

		T instance;
		try {
			instance = injectableConstructor.instantiate(parameters);
		} catch (IllegalArgumentException | InvocationTargetException | InstantiationException
				| IllegalAccessException e) {
			throw new InjectionException("Unable to instantiate the type '" + set.type.getSimpleName()
					+ "' with constructor '" + injectableConstructor.getConstructor() + "'", e);
		}

		handleDestroying(injectionChain, set, instance, applicators.getPostProcessorsOfPhase(Phase.DESTROY),
				isAllocatedInjection);

		registerFinalizers(instance, injectionChain, applicators.getPostProcessorsOfPhase(Phase.FINALIZE));

		process(instance, set.type, injectionChain, applicators.getPostProcessorsOfPhase(Phase.INSPECT));

		for (ResolvableField resolvableField : ReflectionCache.getResolvableFields(set.type)) {
			Field field = resolvableField.getField();
			ResolvingSettings fieldSet = resolvableField.getSettings();

			String property = injectionChain.resolve(fieldSet);

			try {
				field.set(instance, property);
			} catch (IllegalAccessException e) {
				throw new InjectionException("Unable to resolve field '" + field.getName() + "' of the type "
						+ field.getDeclaringClass().getSimpleName() + "; unable to gain access", e);
			}
		}

		process(instance, set.type, injectionChain, applicators.getPostProcessorsOfPhase(Phase.CONSTRUCT));

		for (InjectableField injectableField : ReflectionCache.getInjectableFields(set.type)) {
			Field field = injectableField.getField();
			InjectionSettings<?> fieldSet = injectableField.getSettings();

			Object bean = instantiate(injectionChain, fieldSet);

			if (bean != null || fieldSet.overwriteWithNull) {
				try {
					field.set(instance, bean);
				} catch (IllegalArgumentException e) {
					throw new InjectionException(
							"Unable to set instance of type " + instance.getClass().getName() + " to field '"
									+ field.getName() + "' of the type " + field.getDeclaringClass().getSimpleName()
									+ ", whose type is " + field.getType().getSimpleName(),
							e);
				} catch (IllegalAccessException e) {
					throw new InjectionException("Unable to inject field '" + field.getName() + "' of the type "
							+ field.getDeclaringClass().getSimpleName() + "; unable to gain access", e);
				}
			}
		}

		process(instance, set.type, injectionChain, applicators.getPostProcessorsOfPhase(Phase.INJECT));

		return instance;
	}

	private <T> void handleDestroying(InjectionChain chain, InjectionSettings<?> set, T instance,
			List<Processor<? super T>> destroyers, boolean isAllocatedInjection) {
		if (set.isIndependent) {
			registerDestroyers(instance, chain, destroyers);
		} else if (set.singletonMode == SingletonMode.SEQUENCE) {
			registerDestroyers(instance, chain, destroyers);
			chain.addSingleton(set.qualifier, instance, isAllocatedInjection);
		} else {
			this.globalInjectionContext.addGlobalSingleton(set.qualifier, instance, destroyers);
		}
	}

	private <T> void registerDestroyers(T instance, InjectionChain injectionChain,
			List<Processor<? super T>> destroyers) {
		for (Processor<? super T> destroyer : destroyers) {
			injectionChain.addDestoryable(() -> destroyer.process(instance, null));
		}
	}

	private <T> void registerFinalizers(T instance, InjectionChain injectionChain,
			List<Processor<? super T>> finalizers) {
		for (Processor<? super T> finalizer : finalizers) {
			injectionChain.addFinalizable(() -> finalizer.process(instance, null));
		}
	}

	private <T> void process(T instance, Class<T> type, InjectionChain chain,
			List<Processor<? super T>> injectionProcessors) {
		for (Processor<? super T> postProcessor : injectionProcessors) {
			TemporalInjectorCallback callback = new TemporalInjectorCallback(chain);
			try {
				postProcessor.process(instance, callback);
			} catch (Exception e) {
				throw new ProcessorException("Unable to process instance of '" + type.getSimpleName()
						+ "'; the processing threw an exception", e);
			} finally {
				callback.deactivate();
			}
		}
	}

	/**
	 * Destroys the given root bean.
	 * <p>
	 * This {@link Method} can be applied on all root beans that have been
	 * instantiated using this {@link Injector} and will destroy their sub beans as
	 * well.
	 * <p>
	 * That being said, calling this {@link Method} will cause all yet undestroyed
	 * child {@link Injector}s that are sub beans to the given bean to be destroyed,
	 * who will then destroy their undestroyed child {@link Injector}s and so on.
	 * <p>
	 * In other words, calling this {@link Method} effectively destroys the
	 * injection sub tree identified by the given bean as its root.
	 * 
	 * @param rootBean
	 *            The root bean to destroy; 'root' means the given object has to be
	 *            the root bean that was instantiated, injected and returned by this
	 *            {@link Injector}, not a sub bean of such a bean.
	 */
	public void destroy(Object rootBean) {
		if (this.beans.containsKey(rootBean)) {
			destroy(rootBean, this.beans.get(rootBean));
			this.beans.remove(rootBean);
		} else {
			throw new IllegalArgumentException(
					"The given " + rootBean.getClass().getSimpleName() + " (@" + System.identityHashCode(rootBean)
							+ ") instance has not been directly instantiated by this injector.");
		}
	}

	@Process(Phase.DESTROY)
	private synchronized void releaseReferences() {
		destroyAll();
	}

	/**
	 * Shorthand for calling {@link #destroy(Object)} with all beans that have been
	 * instantiated by this {@link Injector} at the moment of calling.
	 */
	public void destroyAll() {
		Iterator<Entry<Object, List<SelfSustaningProcessor>>> beanIter = this.beans.entrySet().iterator();
		while (beanIter.hasNext()) {
			Entry<Object, List<SelfSustaningProcessor>> entry = beanIter.next();
			destroy(entry.getKey(), entry.getValue());
			beanIter.remove();
		}
	}

	private void destroy(Object bean, List<SelfSustaningProcessor> destroyables) {
		for (SelfSustaningProcessor destroyable : destroyables) {
			try {
				destroyable.process();
			} catch (Exception e) {
				throw new ProcessorException("Unable to destroy injected " + bean.getClass().getSimpleName()
						+ " instance (@" + System.identityHashCode(bean) + ")", e);
			}
		}
	}

	/**
	 * Factory {@link Method} for an {@link Injector}.
	 * <p>
	 * This {@link Method} is the only possibility to create an {@link Injector}
	 * manually.
	 * <p>
	 * Creating a {@link Injector} manually causes the {@link Injector} not to have
	 * a parent {@link Injector}, which effectively makes 'create an
	 * {@link Injector} manually' equivalent to 'beginning a new injection tree'.
	 * <p>
	 * This is the reason why the returned type is {@link RootInjector} (not
	 * {@link Injector}), as the injection tree's root {@link Injector} offers some
	 * special functionality like predefining {@link SingletonMode#GLOBAL}
	 * {@link Singleton}s.
	 * 
	 * @param predefinables
	 *            The {@link Predefinable}s to globally use in all sub injections
	 *            down the injection tree, such as {@link SingletonMode#GLOBAL}
	 *            {@link Singleton}s or {@link Property}s; might be null or contain
	 *            nulls, both is ignored.
	 * 
	 * @return A new {@link RootInjector} instance; never null
	 */
	public static RootInjector of(Predefinable... predefinables) {
		return of(Arrays.asList(predefinables));
	}

	/**
	 * Factory {@link Method} for a {@link RootInjector}.
	 * <p>
	 * This {@link Method} is the only possibility to create an {@link Injector}
	 * manually.
	 * <p>
	 * Creating an {@link Injector} manually causes the {@link Injector} not to have
	 * a parent {@link Injector}, which effectively makes 'create an
	 * {@link Injector} manually' equivalent to 'beginning a new injection tree'.
	 * <p>
	 * This is the reason why the returned type is {@link RootInjector} (not
	 * {@link Injector}), as the injection tree's root {@link Injector} offers some
	 * special functionality like predefining {@link SingletonMode#GLOBAL}
	 * {@link Singleton}s and destroying the whole injection tree.
	 * 
	 * @param predefinables
	 *            The {@link Predefinable}s to globally use in all sub injections
	 *            down the injection tree, such as {@link SingletonMode#GLOBAL}
	 *            {@link Singleton}s or {@link Property}s; might be null or contain
	 *            nulls, both is ignored.
	 * 
	 * @return A new {@link RootInjector} instance; never null
	 */
	public static RootInjector of(Collection<Predefinable> predefinables) {
		ResolvingContext globalResolvingContext = new ResolvingContext();
		MappingContext globalMappingContext = new MappingContext();
		TypeContext globalTypeContext = new TypeContext();
		GlobalInjectionContext globalInjectionContext = new GlobalInjectionContext(globalResolvingContext,
				globalMappingContext, globalTypeContext);

		RootInjector injector = new RootInjector(globalInjectionContext, globalResolvingContext, globalMappingContext,
				globalTypeContext);

		if (predefinables != null) {
			Map<String, AbstractAllocator<?>> globalSingletonAllocations = new HashMap<>();

			for (Predefinable predefinable : predefinables) {
				if (predefinable != null) {
					if (predefinable instanceof Property) {
						Property property = (Property) predefinable;
						String propertyKey = property.getKey();
						if (globalResolvingContext.hasProperty(propertyKey)) {
							throw new IllegalArgumentException(
									"There were 2 or more property values defined for the key '" + propertyKey + "'; '"
											+ globalResolvingContext.getProperty(propertyKey) + "' and '"
											+ property.getValue() + "'");
						}
						globalResolvingContext.addProperty(propertyKey, property.getValue());
					} else if (predefinable instanceof Singleton) {
						Singleton singleton = ((Singleton) predefinable);
						if (globalSingletonAllocations.containsKey(singleton.getQualifier())) {
							throw new IllegalArgumentException(
									"There were 2 or more singletons defined for the qualifier '"
											+ singleton.getQualifier() + "'");
						}
						globalSingletonAllocations.put(singleton.getQualifier(), singleton.getAllocator());
					} else if (predefinable instanceof Mapping) {
						Mapping mapping = (Mapping) predefinable;
						String mappingBase = mapping.getBase();
						if (globalMappingContext.hasMapping(mappingBase, mapping.getMode())) {
							throw new IllegalArgumentException(
									"There were 2 or more singleton mapping targets defined for the mapping base '"
											+ mappingBase + "'; '" + globalResolvingContext.getProperty(mappingBase)
											+ "' and '" + mapping.getTarget() + "'");
						}
						globalMappingContext.addMapping(mappingBase, mapping.getTarget(), mapping.getMode());
					}
				}
			}

			InjectionChain chain = InjectionChain.forGlobalSingletonResolving(globalInjectionContext,
					globalSingletonAllocations);
			((Injector) injector).resolveSingletonsIntoChain(chain, globalSingletonAllocations.keySet(),
					SingletonMode.GLOBAL);
		}

		return injector;
	}

	private void resolveSingletonsIntoChain(InjectionChain chain, Set<String> qualifiers, SingletonMode mode) {
		for (String qualifier : qualifiers) {
			InjectionSettings<Object> set = InjectionSettings.of(qualifier, mode);
			instantiate(chain, set);
		}
	}
}
