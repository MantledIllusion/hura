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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import com.mantledillusion.injection.hura.BeanAllocation.BeanProvider;
import com.mantledillusion.injection.hura.Blueprint.BlueprintTemplate;
import com.mantledillusion.injection.hura.Blueprint.TypedBlueprint;
import com.mantledillusion.injection.hura.Predefinable.Property;
import com.mantledillusion.injection.hura.Predefinable.Singleton;
import com.mantledillusion.injection.hura.Processor.Phase;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableConstructor;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableConstructor.ParamSettingType;
import com.mantledillusion.injection.hura.ReflectionCache.InjectableField;
import com.mantledillusion.injection.hura.ReflectionCache.ResolvableField;
import com.mantledillusion.injection.hura.annotation.Construct;
import com.mantledillusion.injection.hura.annotation.Context;
import com.mantledillusion.injection.hura.annotation.Inject;
import com.mantledillusion.injection.hura.annotation.Inject.InjectionMode;
import com.mantledillusion.injection.hura.annotation.Inject.SingletonMode;
import com.mantledillusion.injection.hura.annotation.Process;
import com.mantledillusion.injection.hura.exception.ProcessorException;
import com.mantledillusion.injection.hura.exception.ResolvingException;
import com.mantledillusion.injection.hura.exception.ValidatorException;
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
public class Injector {

	/**
	 * The root {@link Injector} of an injection tree.
	 * <p>
	 * This specific {@link Injector} type defines and holds the
	 * {@link SingletonMode#GLOBAL} {@link InjectionContext} that contains those
	 * {@link Singleton}s that need to be available to the whole injection tree.
	 */
	public static final class RootInjector extends Injector {

		private RootInjector(InjectionContext globalInjectionContext, ResolvingContext resolvingContext) {
			super(globalInjectionContext, resolvingContext);
		}
	}

	/**
	 * A temporarily valid injection callback that can be used to instantiate a bean
	 * during a currently running injection sequence of an {@link Injector}.
	 * <p>
	 * The callback automatically looses its instantiation ability when the
	 * injection sequence proceeds; any successive calls on the callback's functions
	 * will result in {@link IllegalStateException}s. The current state can be
	 * checked with {@link TemporalInjectorCallback#isActive()}.
	 */
	public final class TemporalInjectorCallback {

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
		 *         returned, all calls to {@link #instantiate(Class)},
		 *         {@link #instantiate(TypedBlueprint)} or any of the
		 *         {@link #resolve(String)} {@link Method}s will fail with
		 *         {@link IllegalStateException}s
		 */
		public boolean isActive() {
			return this.isActive;
		}

		private void deactivate() {
			this.isActive = false;
		}

		/**
		 * Convenience {@link Method} for not having to use
		 * {@link #instantiate(TypedBlueprint)} with the result of
		 * {@link Blueprint#of(Class, Predefinable...)} (without using any
		 * {@link Predefinable}s).
		 * 
		 * @param <T>
		 *            The bean type.
		 * @param clazz
		 *            The {@link Class} to instantiate and inject; might <b>not</b> be
		 *            null.
		 * @return An injected instance of the given {@link Class}; never null
		 */
		public <T> T instantiate(Class<T> clazz) {
			return instantiate(Blueprint.of(clazz));
		}

		/**
		 * Instantiates an injects an instance of the given {@link TypedBlueprint}'s
		 * root type.
		 * 
		 * @param <T>
		 *            The bean type.
		 * @param blueprint
		 *            The {@link TypedBlueprint} to use for instantiation and injection;
		 *            might <b>not</b> be null.
		 * @return An injected instance of the given {@link TypedBlueprint}'s root type;
		 *         never null
		 */
		public <T> T instantiate(TypedBlueprint<T> blueprint) {
			checkActive();

			InjectionChain chain = this.chain.extendBy(blueprint);
			InjectionSettings<T> set = InjectionSettings.of(blueprint);
			return Injector.this.instantiate(chain, set);
		}

		/**
		 * Resolves the given property key.
		 * <p>
		 * Resolving is not forced.
		 * 
		 * @param propertyKey
		 *            The key to resolve; might <b>not</b> be null or empty.
		 * @return The property value, never null
		 */
		public String resolve(String propertyKey) {
			return resolve(propertyKey, com.mantledillusion.injection.hura.annotation.Property.DEFAULT_MATCHER, false);
		}

		/**
		 * Resolves the given property key.
		 * <p>
		 * Resolving might be forced if desired.
		 * 
		 * @param propertyKey
		 *            The key to resolve; might <b>not</b> be null or empty.
		 * @param forced
		 *            Determines whether the resolving has to be successful. If set to
		 *            true, a {@link ResolvingException} will be thrown if the key
		 *            cannot be resolved.
		 * @return The property value, never null
		 */
		public String resolve(String propertyKey, boolean forced) {
			return resolve(propertyKey, com.mantledillusion.injection.hura.annotation.Property.DEFAULT_MATCHER, forced);
		}

		/**
		 * Resolves the given property key.
		 * <p>
		 * Resolving might be forced if desired.
		 * 
		 * @param propertyKey
		 *            The key to resolve; might <b>not</b> be null or empty.
		 * @param matcher
		 *            The matcher for the property value; might <b>not</b> be null, must
		 *            be parsable by {@link Pattern#compile(String)}.
		 * @param forced
		 *            Determines whether the resolving has to be successful. If set to
		 *            true, a {@link ResolvingException} will be thrown if the key
		 *            cannot be resolved.
		 * @return The property value, never null
		 */
		public String resolve(String propertyKey, String matcher, boolean forced) {
			checkActive();
			checkKey(propertyKey);
			checkMatcher(matcher, null);

			ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, forced);
			return Injector.this.resolve(this.chain, set);
		}

		/**
		 * Resolves the given property key.
		 * <p>
		 * Resolving is not forced; if the property cannot be resolved, the given
		 * default value is used.
		 * 
		 * @param propertyKey
		 *            The key to resolve; might <b>not</b> be null or empty.
		 * @param defaultValue
		 *            The default value to return if the key cannot be resolved; might
		 *            <b>not</b> be null.
		 * @return The property value, never null
		 */
		public String resolve(String propertyKey, String defaultValue) {
			return resolve(propertyKey, com.mantledillusion.injection.hura.annotation.Property.DEFAULT_MATCHER,
					defaultValue);
		}

		/**
		 * Resolves the given property key.
		 * <p>
		 * Resolving is not forced; if the property cannot be resolved or the matcher
		 * fails, the given default value is used.
		 * 
		 * @param propertyKey
		 *            The key to resolve; might <b>not</b> be null or empty.
		 * @param matcher
		 *            The matcher for the property value; might <b>not</b> be null, must
		 *            be parsable by {@link Pattern#compile(String)}.
		 * @param defaultValue
		 *            The default value to return if the key cannot be resolved or the
		 *            value does not match the matcher's pattern; might <b>not</b> be
		 *            null.
		 * @return The property value, never null
		 */
		public String resolve(String propertyKey, String matcher, String defaultValue) {
			checkActive();
			if (defaultValue == null) {
				throw new IllegalArgumentException("Cannot fall back to a null default value.");
			}
			checkKey(propertyKey);
			checkMatcher(matcher, defaultValue);

			ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, defaultValue);
			return Injector.this.resolve(this.chain, set);
		}

		private void checkKey(String propertyKey) {
			if (StringUtils.isEmpty(propertyKey)) {
				throw new IllegalArgumentException("Cannot resolve a property using a null key.");
			}
		}

		private void checkMatcher(String matcher, String defaultValue) {
			Pattern pattern;
			try {
				pattern = Pattern.compile(matcher);
			} catch (PatternSyntaxException | NullPointerException e) {
				throw new IllegalArgumentException(
						"The matcher  '" + matcher + "' is no valid pattern: " + e.getMessage(), e);
			}

			if (defaultValue != null && !pattern.matcher(defaultValue).matches()) {
				throw new ValidatorException("The the default value '" + defaultValue
						+ "' does not match the specified matcher pattern '" + matcher + "'.");
			}
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
			injector.defineSingletonIfNecessary(injectionChain, set, this.instance);
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
			injector.defineSingletonIfNecessary(injectionChain, set, bean);
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
			return injector.createAndInject(chain, refinedSettings, this.applicators.merge(refinedApplicators));
		}
	}

	interface SelfSustaningProcessor {

		void process() throws Exception;
	}

	private final InjectionContext globalInjectionContext;
	private final InjectionContext baseInjectionContext;
	private final ResolvingContext resolvingContext;

	private final IdentityHashMap<Object, List<SelfSustaningProcessor>> beans = new IdentityHashMap<>();

	@Construct
	private Injector(
			@Inject(value = InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, singletonMode = SingletonMode.GLOBAL) InjectionContext globalInjectionContext,
			@Inject(value = InjectionContext.INJECTION_CONTEXT_SINGLETON_ID, singletonMode = SingletonMode.SEQUENCE) InjectionContext baseInjectionContext,
			@Inject(value = ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID, singletonMode = SingletonMode.SEQUENCE) ResolvingContext resolvingContex) {
		this.globalInjectionContext = globalInjectionContext;
		this.baseInjectionContext = baseInjectionContext;
		this.resolvingContext = resolvingContex;
	}

	private Injector(InjectionContext globalInjectionContext, ResolvingContext resolvingContext) {
		this.globalInjectionContext = globalInjectionContext;
		this.baseInjectionContext = new InjectionContext();
		this.resolvingContext = resolvingContext;
	}

	/**
	 * Convenience {@link Method} for not having to use
	 * {@link #instantiate(TypedBlueprint)} with the result of
	 * {@link Blueprint#of(Class, Predefinable...)} (without using any
	 * {@link Predefinable}s).
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param clazz
	 *            The {@link Class} to instantiate and inject; might <b>not</b> be
	 *            null.
	 * @return An injected instance of the given {@link Class}; never null
	 */
	public <T> T instantiate(Class<T> clazz) {
		return instantiate(Blueprint.of(clazz));
	}

	/**
	 * Instantiates and injects an instance of the given {@link TypedBlueprint}'s
	 * root type.
	 * 
	 * @param <T>
	 *            The bean type.
	 * @param blueprint
	 *            The {@link TypedBlueprint} to use for instantiation and injection;
	 *            might <b>not</b> be null.
	 * @return An injected instance of the given {@link TypedBlueprint}'s root type;
	 *         never null
	 */
	public final <T> T instantiate(TypedBlueprint<T> blueprint) {
		if (blueprint == null) {
			throw new IllegalArgumentException("Unable to inject using a null blueprint.");
		}

		ResolvingContext resolvingContext = new ResolvingContext(this.resolvingContext);
		blueprint.getPropertyAllocations().forEach((key, value) -> resolvingContext.addProperty(key, value));

		InjectionContext injectionContext = resolveSingletons(this.baseInjectionContext, resolvingContext,
				blueprint.getSingletonAllocations(), SingletonMode.SEQUENCE);

		InjectionChain chain = InjectionChain.forInjection(blueprint.getTypeAllocations(), injectionContext,
				resolvingContext);
		InjectionSettings<T> settings = InjectionSettings.of(blueprint);

		T instance;
		try {
			instance = instantiate(chain, settings);
			this.beans.put(instance, chain.getDestroyables());
			finalize(chain.getFinalizables());
		} catch (Exception e) {
			for (SelfSustaningProcessor destroyable : chain.getDestroyables()) {
				try {
					destroyable.process();
				} catch (Exception e1) {
					// Do nothing; If proper injection has failed, destorying might fail as well.
				}
			}
			throw e;
		}
		return instance;
	}

	private void finalize(List<SelfSustaningProcessor> finalizables) {
		Collections.reverse(finalizables);
		for (SelfSustaningProcessor finalizable : finalizables) {
			try {
				finalizable.process();
			} catch (Exception e) {
				throw new ProcessorException("Unable to finalize; the processing threw an exception: " + e.getMessage(),
						e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T instantiate(InjectionChain injectionChain, InjectionSettings<T> set) {
		if (set.isContext && injectionChain.isChildOfGlobalSingleton()) {
			throw new InjectionException("Cannot refer to the type '" + set.type.getSimpleName()
					+ "' as it (or one of its super classes/interfaces) is marked with the "
					+ Context.class.getSimpleName()
					+ " annotation; context instances cannot be a sub bean of global singletons, but it is at "
					+ injectionChain.getStringifiedChainSinceDependency());
		}

		if (!set.extensions.isEmpty()) {
			injectionChain = applyExtensions(injectionChain, set.extensions);
		}

		T instance = null;

		if (set.isIndependent) {
			if (injectionChain.hasTypeAllocator(set.type)) {
				instance = ((AbstractAllocator<T>) injectionChain.getTypeAllocator(set.type)).allocate(this,
						injectionChain, set, buildApplicators(injectionChain, set));
			} else if (set.injectionMode == InjectionMode.EAGER) {
				instance = createAndInject(injectionChain, set, buildApplicators(injectionChain, set));
			}
		} else {
			Object singleton = null;
			if (set.singletonMode == SingletonMode.GLOBAL
					&& this.globalInjectionContext.hasSingleton(set.singletonId)) {
				singleton = this.globalInjectionContext.retrieveSingleton(set.singletonId);
			} else if (set.singletonMode == SingletonMode.SEQUENCE && injectionChain.hasSingleton(set.singletonId)) {
				singleton = injectionChain.retrieveSingleton(set.singletonId);
			} else if (injectionChain.hasSingletonAllocator(set.singletonId)) {
				singleton = ((AbstractAllocator<T>) injectionChain.getSingletonAllocator(set.singletonId))
						.allocate(this, injectionChain, set, buildApplicators(injectionChain, set));
			} else if (set.injectionMode == InjectionMode.EAGER) {
				singleton = createAndInject(injectionChain, set, buildApplicators(injectionChain, set));
			}

			if (singleton != null) {
				if (TypeUtils.isAssignable(singleton.getClass(), set.type)) {
					instance = (T) singleton;
				} else {
					throw new InjectionException("The singleton with the id '" + set.singletonId
							+ "' needs to be injected with an assignable of the type " + set.type.getSimpleName()
							+ ", but there already exists a singleton of that name with the type "
							+ singleton.getClass().getSimpleName() + " who is not assignable.");
				}
			}
		}

		if (instance == null) {
			return null;
		}

		return instance;
	}

	private InjectionChain applyExtensions(InjectionChain chain, List<Class<? extends BlueprintTemplate>> extensions) {
		List<Blueprint> parsed = new ArrayList<>();
		for (Class<? extends BlueprintTemplate> extension : extensions) {
			if (extension != null) {
				BlueprintTemplate instantiated = instantiate(chain, InjectionSettings.of(Blueprint.of(extension)));
				parsed.add(Blueprint.from(instantiated));
			}
		}
		return chain.extendBy(parsed);
	}

	private <T> InjectionProcessors<T> buildApplicators(InjectionChain chain, InjectionSettings<T> set) {
		ReflectionCache.validate(set.type);
		TemporalInjectorCallback callback = new TemporalInjectorCallback(chain);
		InjectionProcessors<T> applicators = InjectionProcessors.of(set.type, callback);
		callback.deactivate();
		return applicators;
	}

	private <T> T createAndInject(InjectionChain injectionChain, InjectionSettings<T> set,
			InjectionProcessors<T> applicators) {
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
			injectionChain = InjectionChain.forGlobalSingletonInjection(this.resolvingContext);
		} else {
			injectionChain = injectionChain.extendBy(injectableConstructor.getConstructor(), set.isIndependent,
					set.singletonMode, set.singletonId);
		}

		Object[] parameters = new Object[injectableConstructor.getParamCount()];
		for (int i = 0; i < injectableConstructor.getParamCount(); i++) {
			ParamSettingType type = injectableConstructor.getSettingTypeOfParam(i);
			if (type == ParamSettingType.RESOLVABLE || type == ParamSettingType.BOTH) {
				parameters[i] = resolve(injectionChain, injectableConstructor.getResolvingSettings(i));
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
					+ "' with constructor '" + injectableConstructor.getConstructor() + "': " + e.getMessage(), e);
		}
		registerDestroyers(instance, set.isIndependent, injectionChain,
				applicators.getPostProcessorsOfPhase(Phase.DESTROY));
		registerFinalizers(instance, set.isIndependent, injectionChain,
				applicators.getPostProcessorsOfPhase(Phase.FINALIZE));

		defineSingletonIfNecessary(injectionChain, set, instance);

		process(instance, set.type, injectionChain, applicators.getPostProcessorsOfPhase(Phase.INSPECT));

		for (ResolvableField resolvableField : ReflectionCache.getResolvableFields(set.type)) {
			Field field = resolvableField.getField();
			ResolvingSettings fieldSet = resolvableField.getSettings();

			String property = resolve(injectionChain, fieldSet);

			try {
				field.set(instance, property);
			} catch (IllegalArgumentException e) {
				throw new InjectionException("Unable to set property '" + property + "' to field '" + field.getName()
						+ "' of the type " + field.getDeclaringClass().getSimpleName() + ", whose type is "
						+ field.getType().getSimpleName(), e);
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

	private String resolve(InjectionChain injectionChain, ResolvingSettings set) {
		if (injectionChain.hasProperty(set.propertyKey)) {
			String property = injectionChain.getProperty(set.propertyKey);
			if (!property.matches(set.matcher)) {
				if (!set.forced && set.useDefault) {
					return set.defaultValue;
				} else {
					throw new ResolvingException("The defined property '" + set.propertyKey + "' is set to the value '"
							+ property + "', which does not match the required pattern '" + set.matcher + "'.");
				}
			}
			return property;
		} else if (set.forced) {
			throw new ResolvingException("The property '" + set.propertyKey + "' is not set, but is required to be.");
		} else if (set.useDefault) {
			return set.defaultValue;
		} else {
			return set.propertyKey;
		}
	}

	private <T> void defineSingletonIfNecessary(InjectionChain chain, InjectionSettings<?> set, T instance) {
		if (!set.isIndependent) {
			if (set.singletonMode == SingletonMode.SEQUENCE) {
				chain.addSingleton(set.singletonId, instance);
			} else {
				this.globalInjectionContext.addSingleton(set.singletonId, instance);
			}
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
						+ "'; the processing threw an exception: " + e.getMessage(), e);
			} finally {
				callback.deactivate();
			}
		}
	}

	private <T> void registerFinalizers(T instance, boolean isIndependent, InjectionChain injectionChain,
			List<Processor<? super T>> finalizers) {
		for (Processor<? super T> finalizer : finalizers) {
			injectionChain.addFinalizable(() -> finalizer.process(instance, null));
		}
	}

	private <T> void registerDestroyers(T instance, boolean isIndependent, InjectionChain injectionChain,
			List<Processor<? super T>> destroyers) {
		for (Processor<? super T> destroyer : destroyers) {
			injectionChain.addDestoryable(() -> destroyer.process(instance, null));
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
				throw new RuntimeException("Unable to destroy injected " + bean.getClass().getSimpleName()
						+ " instance (@" + System.identityHashCode(bean) + "): " + e.getMessage(), e);
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
		InjectionContext globalInjectionContext = new InjectionContext();
		ResolvingContext globalResolvingContext = new ResolvingContext();

		RootInjector injector = new RootInjector(globalInjectionContext, globalResolvingContext);

		if (predefinables != null) {
			Map<String, AbstractAllocator<?>> singletonAllocations = new HashMap<>();

			for (Predefinable predefinable : predefinables) {
				if (predefinable != null) {
					if (predefinable instanceof Singleton) {
						Singleton singleton = ((Singleton) predefinable);
						if (singletonAllocations.containsKey(singleton.getSingletonId())) {
							throw new IllegalArgumentException(
									"There were 2 or more singletons defined for the singletonId '"
											+ singleton.getSingletonId() + "'");
						}
						singletonAllocations.put(singleton.getSingletonId(), singleton.getAllocator());
					} else if (predefinable instanceof Property) {
						Property property = (Property) predefinable;
						String propertyKey = property.getKey();
						if (globalResolvingContext.hasProperty(propertyKey)) {
							throw new IllegalArgumentException(
									"There were 2 or more property values defined for the key '" + propertyKey + "'; '"
											+ globalResolvingContext.getProperty(propertyKey) + "' and '"
											+ property.getValue() + "'");
						}
						globalResolvingContext.addProperty(propertyKey, property.getValue());
					}
				}
			}

			((Injector) injector).resolveSingletons(globalInjectionContext, globalResolvingContext,
					singletonAllocations, SingletonMode.GLOBAL);
		}

		return injector;
	}

	private InjectionContext resolveSingletons(InjectionContext baseContext, ResolvingContext resolvingContext,
			Map<String, AbstractAllocator<?>> singletonAllocations, SingletonMode mode) {
		InjectionChain chain = InjectionChain.forSingletonResolving(singletonAllocations, baseContext,
				resolvingContext);
		for (String singletonId : singletonAllocations.keySet()) {
			InjectionSettings<Object> set = InjectionSettings.of(singletonId, mode);
			instantiate(chain, set);
		}
		return chain.getContext();
	}
}
