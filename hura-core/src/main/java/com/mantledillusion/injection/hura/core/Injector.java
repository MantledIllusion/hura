package com.mantledillusion.injection.hura.core;

import com.mantledillusion.essentials.object.ListEssentials;
import com.mantledillusion.injection.hura.core.ReflectionCache.AggregateableField;
import com.mantledillusion.injection.hura.core.ReflectionCache.InjectableConstructor;
import com.mantledillusion.injection.hura.core.ReflectionCache.InjectableConstructor.ParamSettingType;
import com.mantledillusion.injection.hura.core.ReflectionCache.InjectableField;
import com.mantledillusion.injection.hura.core.ReflectionCache.ResolvableField;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.core.annotation.instruction.Context;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.Phase;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PreDestroy;
import com.mantledillusion.injection.hura.core.exception.AggregationException;
import com.mantledillusion.injection.hura.core.exception.InjectionException;
import com.mantledillusion.injection.hura.core.exception.ProcessorException;
import com.mantledillusion.injection.hura.core.exception.ShutdownException;
import com.mantledillusion.injection.hura.core.service.AggregationProvider;
import com.mantledillusion.injection.hura.core.service.InjectionProvider;
import com.mantledillusion.injection.hura.core.service.ResolvingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * An {@link Injector} for instantiating and injecting beans.
 * <p>
 * The root {@link Injector} of an injection tree may be instantiated manually
 * using any method of...<br>
 * - {@link #of()}<br>
 * - {@link #of(Blueprint.Allocation, Blueprint.Allocation...)}<br>
 * - {@link #of(Blueprint, Blueprint...)}<br>
 * - {@link #of(Collection)}<br>
 * ..., which returns a specific sub type of {@link Injector}; the {@link RootInjector}.
 * All other {@link Injector} instances down the tree (that are needed in beans created by an
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
 * instantiated itself and/or referenced in case it is a {@link Blueprint.SingletonAllocation}.
 * <p>
 * When an {@link Injector} injects another {@link Injector}, it passes its own
 * {@link SingletonContext} onto this child, making it possible that to pass
 * {@link Blueprint.SingletonAllocation} beans further down the injection tree, but not up. As a
 * result, injections by child {@link Injector}s are able to retrieve
 * {@link Blueprint.SingletonAllocation}s from the same pool as their parent in the tree, but cannot
 * define {@link Blueprint.SingletonAllocation}s that will be available to the parent (or another
 * one of its children).
 */
public class Injector implements ResolvingProvider, AggregationProvider, InjectionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Injector.class);

    private enum InjectorState {
        PRE_ACTIVE, ACTIVE, SHUTDOWN;
    }

    /**
     * The root {@link Injector} of an injection tree.
     */
    public static final class RootInjector extends Injector {

        private Map<Phase, List<SelfSustainingProcessor>> rootDestroyables;

        private RootInjector(SingletonContext singletonContext, ResolvingContext resolvingContext,
                             MappingContext mappingContext, TypeContext typeContext) {
            super(singletonContext, resolvingContext, mappingContext, typeContext);
        }

        private void setRootDestroyables(Map<Phase, List<SelfSustainingProcessor>> rootDestroyables) {
            this.rootDestroyables = rootDestroyables;
        }

        /**
         * Extension to {@link #destroyAll()} that not only destroys the whole injection
         * tree, but also all {@link Blueprint.Allocation}s
         * the root injector may have as well.
         *
         * @throws ShutdownException If the instance has already been shut down
         */
        @Override
        public synchronized void shutdown() throws ShutdownException {
            super.shutdown();

            int failingDestructionCount =
                    ((Injector) this).destroy(this, rootDestroyables.get(Phase.PRE_DESTROY), false) +
                            ((Injector) this).destroy(this, rootDestroyables.get(Phase.POST_DESTROY), false);
            this.rootDestroyables.clear();

            if (failingDestructionCount > 0) {
                ((Injector) this).failDestruction(this, failingDestructionCount);
            }
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
     * of incoherent {@link Blueprint.SingletonAllocation} pools, since the currently running injection
     * sequence (which has caused the @{@link Process} method to be called) could
     * define a sequence singleton later on that such an intermediate injection
     * sequence created and finished during the method would not be able to obtain.
     * <p>
     * The callback automatically looses its instantiation ability when the
     * injection sequence proceeds; any successive calls on the callback's functions
     * will result in {@link IllegalStateException}s. The current state can be
     * checked with {@link TemporalInjectorCallback#isActive()}.
     */
    public final class TemporalInjectorCallback implements InjectionProvider, ResolvingProvider {

        private final InjectionChain chain;

        private boolean isShutdown = false;

        private TemporalInjectorCallback(InjectionChain chain) {
            this.chain = chain;
        }

        @Override
        public boolean isActive() {
            return isShutdown;
        }

        private synchronized void shutdown() {
            this.isShutdown = true;
        }

        @Override
        public final String resolve(String propertyKey, String matcher, boolean forced) {
            checkActive();

            InjectionUtils.checkKey(propertyKey);
            InjectionUtils.checkMatcher(matcher, null);

            ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, forced);
            return this.chain.resolve(set);
        }

        @Override
        public <T> T instantiate(Class<T> clazz, Blueprint.Allocation allocation, Blueprint.Allocation... allocations) {
            checkActive();

            InjectionChain chain = this.chain.extendBy(InjectionAllocations.ofAllocations(ListEssentials.toList(allocations, allocation)));
            InjectionSettings<T> set = InjectionSettings.of(clazz);
            return Injector.this.instantiate(chain, set);
        }

        @Override
        public <T> T instantiate(Class<T> clazz, Collection<Blueprint> blueprints) {
            checkActive();

            InjectionChain chain = this.chain.extendBy(InjectionAllocations.ofBlueprints(blueprints));
            InjectionSettings<T> set = InjectionSettings.of(clazz);
            return Injector.this.instantiate(chain, set);
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
            injector.handleDestroying(injectionChain, set, this.instance, true,
                    Collections.emptyList(), Collections.emptyList());
            return this.instance;
        }
    }

    static final class ProviderAllocator<T, T2 extends T> extends AbstractAllocator<T> {

        private final Blueprint.BeanProvider<T2> provider;

        ProviderAllocator(Blueprint.BeanProvider<T2> provider) {
            this.provider = provider;
        }

        @Override
        T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
                   InjectionProcessors<T> applicators) {
            T bean = this.provider.provide(injector.new TemporalInjectorCallback(injectionChain));
            injector.handleDestroying(injectionChain, set, bean, true,
                    Collections.emptyList(), Collections.emptyList());
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
        T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
                   InjectionProcessors<T> applicators) {
            InjectionSettings<T2> refinedSettings = set.refine(this.clazz);
            InjectionProcessors<T2> refinedApplicators = injector.buildApplicators(injectionChain, refinedSettings);
            return injector.createAndInject(injectionChain, refinedSettings, this.applicators.merge(refinedApplicators),
                    true);
        }
    }

    static final class PluginAllocator<T> extends AbstractAllocator<T> {

        private final File directory;
        private final String pluginId;
        private final InjectionProcessors<T> applicators;
        private final Class<T> presetType;

        PluginAllocator(File directory, String pluginId, InjectionProcessors<T> applicators) {
            this.directory = directory;
            this.pluginId = pluginId;
            this.applicators = applicators;
            this.presetType = null;
        }

        PluginAllocator(File directory, String pluginId, Class<T> presetType) {
            this.directory = directory;
            this.pluginId = pluginId;
            this.applicators = InjectionProcessors.of();
            this.presetType = presetType;
        }

        @Override
        T allocate(Injector injector, InjectionChain injectionChain, InjectionSettings<T> set,
                   InjectionProcessors<T> applicators) {
            Class<T> pluggableType = PluginCache.findPluggable(this.directory, this.pluginId, this.presetType == null ? set.type : this.presetType);
            InjectionSettings<T> refinedSettings = set.refine(pluggableType);
            InjectionProcessors<T> refinedApplicators = injector.buildApplicators(injectionChain, refinedSettings);
            return injector.createAndInject(injectionChain, refinedSettings, this.applicators.merge(refinedApplicators),
                    true);
        }
    }

    interface SelfSustainingProcessor {

        void process() throws Exception;
    }

    private final SingletonContext singletonContext;
    private final ResolvingContext resolvingContext;
    private final MappingContext mappingContext;
    private final TypeContext typeContext;

    private final IdentityHashMap<Object, Map<Phase, List<SelfSustainingProcessor>>> beans = new IdentityHashMap<>();

    private InjectorState state = InjectorState.PRE_ACTIVE;

    @Construct
    private Injector(
            @Inject @Qualifier(SingletonContext.INJECTION_CONTEXT_SINGLETON_ID) SingletonContext singletonContext,
            @Inject @Qualifier(ResolvingContext.RESOLVING_CONTEXT_SINGLETON_ID) ResolvingContext resolvingContext,
            @Inject @Qualifier(MappingContext.MAPPING_CONTEXT_SINGLETON_ID) MappingContext mappingContext,
            @Inject @Qualifier(TypeContext.TYPE_CONTEXT_SINGLETON_ID) TypeContext typeContext) {
        this.singletonContext = singletonContext;
        this.resolvingContext = resolvingContext;
        this.mappingContext = mappingContext;
        this.typeContext = typeContext;
    }

    @Override
    public final String resolve(String propertyKey, String matcher, boolean forced) {
        checkActive();
        InjectionUtils.checkKey(propertyKey);
        InjectionUtils.checkMatcher(matcher, null);

        ResolvingSettings set = ResolvingSettings.of(propertyKey, matcher, forced);
        return this.resolvingContext.resolve(set);
    }

    @Override
    public <T> Collection<T> aggregate(Class<T> type, Collection<BiPredicate<String, T>> biPredicates) {
        checkActive();

        return this.singletonContext.aggregate(type, biPredicates);
    }

    @Override
    public final <T> T instantiate(Class<T> clazz, Blueprint.Allocation allocation, Blueprint.Allocation... allocations) {
        return instantiate(clazz, InjectionAllocations.ofAllocations(ListEssentials.toList(allocations, allocation)));
    }

    @Override
    public final <T> T instantiate(Class<T> clazz, Collection<Blueprint> blueprints) {
        return instantiate(clazz, InjectionAllocations.ofBlueprints(blueprints));
    }

    private <T> T instantiate(Class<T> type, InjectionAllocations allocations) {
        checkActive();

        InjectionSettings<T> settings = InjectionSettings.of(type);

        InjectionChain chain = InjectionChain.forInjection(this.singletonContext.getInjectionTreeLock(),
                this.singletonContext, this.resolvingContext, this.mappingContext, this.typeContext, allocations);

        return resolveSingletonsAndPerform(chain, destroyables -> {
            T instance = instantiate(chain, settings);
            Injector.this.beans.put(instance, destroyables);
            return instance;
        });
    }

    private <T> T resolveSingletonsAndPerform(InjectionChain chain, Function<Map<Phase, List<SelfSustainingProcessor>>, T> function) {
        T instance = null;
        try {
            resolveSingletonsIntoChain(chain);

            Map<Phase, List<SelfSustainingProcessor>> destroyables = new HashMap<>();
            destroyables.put(Phase.PRE_DESTROY, chain.getPreDestroyables());
            destroyables.put(Phase.POST_DESTROY, chain.getPostDestroyables());

            instance = function.apply(destroyables);

            finalize(chain.getAggregateables());
            finalize(chain.getActivateables());
            finalize(chain.getPostConstructables());
        } catch (Exception e) {
            chain.clearHook();

            int failingDestructionCount = destroy(instance, chain.getPreDestroyables(), false) +
                    destroy(instance, chain.getPostDestroyables(), false);

            if (failingDestructionCount > 0) {
                failDestruction(instance, failingDestructionCount);
            } else {
                throw e;
            }
        }
        return instance;
    }

    private void resolveSingletonsIntoChain(InjectionChain chain) {
        for (String qualifier : chain.getSingletonAllocations()) {
            InjectionSettings<Object> set = InjectionSettings.of(qualifier);
            instantiate(chain, set);
        }
    }

    private void finalize(List<SelfSustainingProcessor> finalizables) {
        Collections.reverse(finalizables);
        for (SelfSustainingProcessor finalizable : finalizables) {
            try {
                finalizable.process();
            } catch (Exception e) {
                throw new ProcessorException("Unable to finalize; the processing threw an exception", e);
            }
        }
    }

    private int destroy(Object bean, List<SelfSustainingProcessor> destroyables, boolean throwOnFailures) {
        int failingDestructionCount = 0;
        Iterator<SelfSustainingProcessor> destroyableIter = destroyables.iterator();
        while (destroyableIter.hasNext()) {
            SelfSustainingProcessor destroyable = destroyableIter.next();
            try {
                destroyable.process();
            } catch (Exception e) {
                failingDestructionCount++;
                LOGGER.warn("[Destruction Processing Fail #]" + failingDestructionCount, e);
                // Do nothing; If proper injection has failed, a failing destroying might just
                // be a consequence.
            } finally {
                if (!throwOnFailures) {
                    destroyableIter.remove();
                }
            }
        }

        if (throwOnFailures && failingDestructionCount > 0) {
            failDestruction(bean, failingDestructionCount);
        }
        return failingDestructionCount;
    }

    private void failDestruction(Object bean, int failingDestructionCount) {
        String beanDescription = bean == null ? "yet uninstantiated bean" :
                "instantiated " + bean.getClass().getSimpleName() + " instance (@" + System.identityHashCode(bean) + ")";
        throw new ProcessorException("Unable to destroy " + beanDescription
                + "; the execution of " + failingDestructionCount + " processors failed");
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiate(InjectionChain injectionChain, InjectionSettings<T> set) {
        InjectionChain hook = injectionChain;
        hook.hookOnThread();

        injectionChain = applyExtensions(injectionChain, set);

        T instance = null;

        if (set.isIndependent) {
            if (injectionChain.hasTypeAllocator(set.type)) {
                instance = ((AbstractAllocator<T>) injectionChain.getTypeAllocator(set.type)).allocate(this,
                        injectionChain, set, buildApplicators(injectionChain, set));
            } else if (set.injectionMode == Optional.InjectionMode.EAGER) {
                instance = createAndInject(injectionChain, set, buildApplicators(injectionChain, set), false);
            }
        } else {
            Object singleton = null;

            if (injectionChain.hasMapping(set.qualifier)) {
                set = set.refine(injectionChain.map(set.qualifier));
            }

            boolean allocatedOnly = set.injectionMode == Optional.InjectionMode.EXPLICIT;
            if (injectionChain.hasSingleton(set.qualifier, set.type, allocatedOnly)) {
                singleton = injectionChain.retrieveSingleton(set.qualifier);
            } else if (injectionChain.hasSingletonAllocator(set.qualifier)) {
                singleton = ((AbstractAllocator<T>) injectionChain.getSingletonAllocator(set.qualifier))
                        .allocate(this, injectionChain, set, buildApplicators(injectionChain, set));
            } else if (!allocatedOnly) {
                singleton = createAndInject(injectionChain, set, buildApplicators(injectionChain, set), false);
            }

            if (singleton != null) {
                if (set.type.isAssignableFrom(singleton.getClass())) {
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
        for (Class<? extends Blueprint> extension : set.extensions) {
            if (extension != null) {
                parsed.add(instantiate(chain, InjectionSettings.of(extension)));
            }
        }
        return chain.extendBy(InjectionAllocations.ofBlueprintsAndAllocations(parsed, set.allocations));
    }

    private <T> InjectionProcessors<T> buildApplicators(InjectionChain chain, InjectionSettings<T> set) {
        TemporalInjectorCallback callback = new TemporalInjectorCallback(chain);
        InjectionProcessors<T> applicators = InjectionProcessors.of(set.type, callback);
        callback.shutdown();

        return applicators;
    }

    private <T> T createAndInject(InjectionChain injectionChain, InjectionSettings<T> set,
                                  InjectionProcessors<T> applicators, boolean isAllocatedInjection) {
        if (set.isContext) {
            throw new InjectionException("Cannot instantiate the type '" + set.type.getSimpleName()
                    + "' as it (or one of its super classes/interfaces) is marked with the "
                    + Context.class.getSimpleName() + " annotation; context objects have to be provided " +
                    "as ready-to-use instances via " + Blueprint.class.getSimpleName());
        } else if (!set.isIndependent && Injector.class.isAssignableFrom(set.type)) {
            throw new InjectionException("Cannot inject an " + Injector.class.getSimpleName()
                    + " as any kind of singleton, as that would allow taking their sequence context out of itself.");
        }

        process(null, set.type, injectionChain, applicators.getProcessorsOfPhase(Phase.PRE_CONSTRUCT));

        InjectableConstructor<T> injectableConstructor = ReflectionCache.getInjectableConstructor(set.type);

        if (injectionChain.containsConstructor(injectableConstructor.getConstructor())) {
            throw new InjectionException("Injection dependency cycle detected: "
                    + injectionChain.getStringifiedChainSinceConstructor(injectableConstructor.getConstructor()));
        }

        injectionChain = injectionChain.extendBy(injectableConstructor.getConstructor(), set);

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

        handleDestroying(injectionChain, set, instance, isAllocatedInjection,
                applicators.getProcessorsOfPhase(Phase.PRE_DESTROY),
                applicators.getProcessorsOfPhase(Phase.POST_DESTROY));

        registerPostConstructProcessors(instance, injectionChain, applicators.getProcessorsOfPhase(Phase.POST_CONSTRUCT));

        for (ResolvableField resolvableField: ReflectionCache.getResolvableFields(set.type)) {
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

        for (InjectableField injectableField: ReflectionCache.getInjectableFields(set.type)) {
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

        process(instance, set.type, injectionChain, applicators.getProcessorsOfPhase(Phase.POST_INJECT));

        for (AggregateableField aggregateableField: ReflectionCache.getAggregateableFields(set.type)) {
            Field field = aggregateableField.getField();
            AggregationSettings<?> fieldSet = aggregateableField.getSettings();

            registerAggregationProcessor(injectionChain, instance, field, fieldSet);
        }

        return instance;
    }

    private <T> void handleDestroying(InjectionChain chain, InjectionSettings<?> set, T instance, boolean isAllocatedInjection,
                                      List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> preDestroyables,
                                      List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> postDestroyables) {
        if (set.isIndependent) {
            registerPreDestroyProcessors(instance, chain, preDestroyables, postDestroyables);
        } else {
            registerPreDestroyProcessors(instance, chain, preDestroyables, postDestroyables);
            chain.addSingleton(set.qualifier, instance, isAllocatedInjection);
        }
    }

    private <T> void registerPreDestroyProcessors(T instance, InjectionChain injectionChain,
                                                  List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> preDestroyables,
                                                  List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> postDestroyables) {
        for (InjectionProcessors.LifecycleAnnotationProcessor<? super T> destroyer : preDestroyables) {
            injectionChain.addPreDestroyable(() -> destroyer.process(instance, null));
        }
        for (InjectionProcessors.LifecycleAnnotationProcessor<? super T> destroyer : postDestroyables) {
            injectionChain.addPostDestroyable(() -> destroyer.process(instance, null));
        }
    }

    private <T> void registerPostConstructProcessors(T instance, InjectionChain injectionChain,
                                                     List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> finalizers) {
        if (instance instanceof Injector) {
            injectionChain.addActivateable(() -> this.state = InjectorState.ACTIVE);
        }
        for (InjectionProcessors.LifecycleAnnotationProcessor<? super T> finalizer : finalizers) {
            injectionChain.addPostConstructables(() -> finalizer.process(instance, null));
        }
    }

    private <T> void process(T instance, Class<T> type, InjectionChain chain,
                             List<InjectionProcessors.LifecycleAnnotationProcessor<? super T>> injectionProcessors) {
        for (InjectionProcessors.LifecycleAnnotationProcessor<? super T> postProcessor : injectionProcessors) {
            TemporalInjectorCallback callback = new TemporalInjectorCallback(chain);
            try {
                postProcessor.process(instance, callback);
            } catch (Exception e) {
                throw new ProcessorException("Unable to process instance of '" + type.getSimpleName()
                        + "'; the processing threw an exception", e);
            } finally {
                callback.shutdown();
            }
        }
    }

    private <T> void registerAggregationProcessor(InjectionChain chain, Object instance, Field field, AggregationSettings<T> fieldSet) {
        chain.addAggregateable(() -> {
            List<BiPredicate<String, T>> predicates = new ArrayList<>();
            if (fieldSet.qualifierMatcher != null) {
                predicates.add((qualifier, bean) -> qualifier.matches(fieldSet.qualifierMatcher));
            }
            for (Class<? extends BiPredicate<String, T>> predicateType: fieldSet.predicates) {
                predicates.add(instantiate(chain, InjectionSettings.of(predicateType)));
            }

            Collection<T> singletons = chain.aggregateSingletons(fieldSet.type, predicates);

            Object parameter = null;
            switch (fieldSet.aggregationMode) {
                case SINGLE:
                    if (singletons.isEmpty()) {
                        if (!fieldSet.optional) {
                            throw new AggregationException("The non-optional field '" + field.getName()
                                    + "' of the type " + field.getDeclaringClass().getSimpleName()
                                    + " requires the aggregation of a single singleton instance, but no candidate is found");
                        }
                    } else if (singletons.size() > 1) {
                        if (!fieldSet.distinct) {
                            throw new AggregationException("The non-distinct field '" + field.getName()
                                    + "' of the type " + field.getDeclaringClass().getSimpleName()
                                    + " requires the aggregation of a single singleton instance, but "
                                    + singletons.size() + " candidates have been found");
                        }
                        parameter = singletons.stream().findFirst().get();
                    } else {
                        parameter = singletons.stream().findFirst().get();
                    }
                    break;
                case LIST:
                    parameter = new ArrayList<>(singletons);
                    break;
                case SET:
                    parameter = new HashSet<>(singletons);
                    break;
            }

            try {
                field.set(instance, parameter);
            } catch (IllegalArgumentException e) {
                throw new AggregationException(
                        "Unable to set instance of type " + parameter.getClass().getName() + " to field '"
                                + field.getName() + "' of the type " + field.getDeclaringClass().getSimpleName()
                                + ", whose type is " + field.getType().getSimpleName(),
                        e);
            } catch (IllegalAccessException e) {
                throw new AggregationException("Unable to aggregate field '" + field.getName() + "' of the type "
                        + field.getDeclaringClass().getSimpleName() + "; unable to gain access", e);
            }
        });
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
     * @param rootBean The root bean to destroy; 'root' means the given object has to be
     *                 the root bean that was instantiated, injected and returned by this
     *                 {@link Injector}, not a sub bean of such a bean.
     */
    public void destroy(Object rootBean) {
        if (this.beans.containsKey(rootBean)) {
            Map<Phase, List<SelfSustainingProcessor>> destroyers = this.beans.get(rootBean);
            destroy(rootBean, destroyers.get(Phase.PRE_DESTROY), true);
            this.beans.remove(rootBean);
            destroy(rootBean, destroyers.get(Phase.POST_DESTROY), true);
        } else {
            throw new IllegalArgumentException(
                    "The given " + rootBean.getClass().getSimpleName() + " (@" + System.identityHashCode(rootBean)
                            + ") instance has not been directly instantiated by this injector.");
        }
    }

    /**
     * Shorthand for calling {@link #destroy(Object)} with all beans that have been
     * instantiated by this {@link Injector} at the moment of calling.
     */
    public void destroyAll() {
        Iterator<Entry<Object, Map<Phase, List<SelfSustainingProcessor>>>> beanIter = this.beans.entrySet().iterator();
        while (beanIter.hasNext()) {
            Entry<Object, Map<Phase, List<SelfSustainingProcessor>>> entry = beanIter.next();
            Object bean = entry.getKey();
            Map<Phase, List<SelfSustainingProcessor>> destroyers = entry.getValue();

            destroy(bean, destroyers.get(Phase.PRE_DESTROY), true);
            beanIter.remove();
            destroy(bean, destroyers.get(Phase.POST_DESTROY), true);
        }
    }

    @Override
    public boolean isActive() {
        return this.state == InjectorState.ACTIVE;
    }

    @PreDestroy
    protected synchronized void shutdown() {
        checkActive();
        destroyAll();
        this.state = InjectorState.SHUTDOWN;
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
     * special functionality that only the root of the injection tree has.
     *
     * @return A new {@link RootInjector} instance; never null
     */
    public static RootInjector of() {
        return of(InjectionAllocations.EMPTY);
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
     * special functionality that only the root of the injection tree has.
     *
     * @param allocation {@link Blueprint.Allocation} to be used during injection, for
     *                    example {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *                    or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null.
     * @param allocations More {@link Blueprint.Allocation}s to be used during injection; might be null or contain nulls.
     * @return A new {@link RootInjector} instance; never null
     */
    public static RootInjector of(Blueprint.Allocation allocation, Blueprint.Allocation... allocations) {
        return of(InjectionAllocations.ofAllocations(ListEssentials.toList(allocations, allocation)));
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
     * special functionality that only the root of the injection tree has.
     *
     * @param blueprint {@link Blueprint} to be used during injection, for
     *                   defining bindings such as {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *                   or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null.
     * @param blueprints More {@link Blueprint}s to be used during injection; might be null or contain nulls.
     * @return A new {@link RootInjector} instance; never null
     */
    public static RootInjector of(Blueprint blueprint, Blueprint... blueprints) {
        return of(ListEssentials.toList(blueprints, blueprint));
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
     * special functionality that only the root of the injection tree has.
     *
     * @param blueprints {@link Blueprint}s to be used during injection, for
     *                   defining bindings such as {@link Blueprint.SingletonAllocation}s, {@link Blueprint.MappingAllocation}s
     *                   or{@link Blueprint.PropertyAllocation}s and {@link Blueprint.TypeAllocation}s; might be null or contain nulls.
     * @return A new {@link RootInjector} instance; never null
     */
    public static RootInjector of(Collection<Blueprint> blueprints) {
        return of(InjectionAllocations.ofBlueprints(blueprints));
    }

    private static RootInjector of(InjectionAllocations allocations) {
        InjectionChain chain = InjectionChain.forRoot(allocations);

        RootInjector injector = new RootInjector(chain.getSingletonContext(), chain.getResolvingContext(),
                chain.getMappingContext(), chain.getTypeContext());

        return ((Injector) injector).resolveSingletonsAndPerform(chain, destroyables -> {
            injector.setRootDestroyables(destroyables);
            return injector;
        });
    }
}
