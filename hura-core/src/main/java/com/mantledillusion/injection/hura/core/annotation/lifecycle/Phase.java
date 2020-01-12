package com.mantledillusion.injection.hura.core.annotation.lifecycle;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;

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
     * - {@link Injector.TemporalInjectorCallback}: <b>YES</b>
     * - @Inject/@Plugin method parameters: <b>NO</b>
     */
    PRE_CONSTRUCT,

    /**
     * The phase where the bean has been constructed, {@link Blueprint.PropertyAllocation}s
     * have been parsed and all sub beans have been injected.
     * <p>
     * The bean is fully initialized at this point, in a sense that it can now be
     * processed in relation to its sub beans.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link Injector.TemporalInjectorCallback}: <b>YES</b>
     * - @Inject/@Plugin method parameters: <b>YES</b>
     */
    POST_INJECT,

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
     * - {@link Injector.TemporalInjectorCallback}: <b>NO</b>
     * - @Inject/@Plugin method parameters: <b>NO</b>
     */
    POST_CONSTRUCT,

    /**
     * The phase right before the processed bean's end-of-life.
     * <p>
     * The bean is ready to be deconstructed at this point, in a sense that it
     * will be ready-for-garbage-collection afterwards.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link Injector.TemporalInjectorCallback}: <b>NO</b>
     * - @Inject/@Plugin annotated method parameters: <b>NO</b>
     */
    PRE_DESTROY,

    /**
     * The phase right after the processed bean's end-of-life.
     * <p>
     * The bean has been deconstructed at this point, it is not even known to the
     * {@link Injector} that injected it anymore.
     * <p>
     * Availability:<br>
     * - Bean: <b>YES</b>
     * - {@link Injector.TemporalInjectorCallback}: <b>NO</b>
     * - @Inject/@Plugin annotated method parameters: <b>NO</b>
     */
    POST_DESTROY
}
