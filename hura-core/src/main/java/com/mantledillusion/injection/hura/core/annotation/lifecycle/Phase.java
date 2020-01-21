package com.mantledillusion.injection.hura.core.annotation.lifecycle;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.service.InjectionProvider;
import com.mantledillusion.injection.hura.core.service.ResolvingProvider;
import com.mantledillusion.injection.hura.core.service.StatefulService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes phases of a bean's lifecycle that are significant for its dependency injection.
 */
public enum Phase {

    /**
     * The phase before the bean is constructed.
     * <p>
     * The bean is not existent at this point, but its {@link Class} type is being inspected.
     * <p>
     * Availability:<br>
     * - Bean: <b>NO</b>
     * - {@link InjectionProvider}: <b>YES</b>
     * - {@link ResolvingProvider}: <b>YES</b>
     * - @Inject/@Plugin method parameters: <b>NO</b>
     */
    PRE_CONSTRUCT(InjectionProvider.class, ResolvingProvider.class),

    /**
     * The phase where the bean has been constructed, {@link Blueprint.PropertyAllocation}s
     * have been parsed and all sub beans have been injected.
     * <p>
     * The bean is fully initialized at this point, in a sense that it can now be
     * processed in relation to its sub beans.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link InjectionProvider}: <b>YES</b>
     * - {@link ResolvingProvider}: <b>YES</b>
     * - @Inject/@Plugin method parameters: <b>YES</b>
     */
    POST_INJECT(InjectionProvider.class, ResolvingProvider.class),

    /**
     * The phase where the bean has been constructed, {@link Blueprint.PropertyAllocation}s
     * have been parsed and all sub <b>and parent</b> beans have been injected.
     * <p>
     * The bean is finalized at this point, in a sense that it can now be
     * ready-for-operation processed in relation to all other beans of its
     * injection sequence.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link InjectionProvider}: <b>NO</b>
     * - {@link ResolvingProvider}: <b>YES</b>
     * - @Inject/@Plugin method parameters: <b>NO</b>
     */
    POST_CONSTRUCT(ResolvingProvider.class),

    /**
     * The phase right before the processed bean's end-of-life.
     * <p>
     * The bean is ready to be deconstructed at this point, in a sense that it
     * will be ready-for-garbage-collection afterwards.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link InjectionProvider}: <b>NO</b>
     * - {@link ResolvingProvider}: <b>YES</b>
     * - @Inject/@Plugin annotated method parameters: <b>NO</b>
     */
    PRE_DESTROY(ResolvingProvider.class),

    /**
     * The phase right after the processed bean's end-of-life.
     * <p>
     * The bean has been deconstructed at this point, it is not even known to the
     * {@link Injector} that injected it anymore.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link InjectionProvider}: <b>NO</b>
     * - {@link ResolvingProvider}: <b>NO</b>
     * - @Inject/@Plugin annotated method parameters: <b>NO</b>
     */
    POST_DESTROY();

    private final Set<Class<? extends StatefulService>> availableServiceTypes;

    @SafeVarargs
    Phase(Class<? extends StatefulService>... availableServiceTypes) {
        this.availableServiceTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(availableServiceTypes)));
    }

    public boolean isAvailable(Class<? extends StatefulService> serviceType) {
        return this.availableServiceTypes.contains(serviceType);
    }
}
