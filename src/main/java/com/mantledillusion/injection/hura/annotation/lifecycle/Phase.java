package com.mantledillusion.injection.hura.annotation.lifecycle;

import com.mantledillusion.injection.hura.Predefinable;

/**
 * Describes phases of the instantiation of a bean for an injection.
 */
public enum Phase {

    PRE_CONSTRUCT,

    /**
     * The phase where the bean has been constructed, {@link Predefinable.Property}s
     * have been parsed and all sub beans have been injected.
     * <p>
     * The bean is fully initialized at this point, in a sense that it can now be
     * processed in relation to its sub beans.
     */
    POST_INJECT,

    /**
     * The phase where the bean has been constructed, {@link Predefinable.Property}s
     * have been parsed and all sub <b>and parent</b> beans have been injected.
     * <p>
     * The bean is finalized at this point, in a sense that it can now be
     * ready-for-operation processed in relation to all other beans of its
     * injection sequence.
     */
    POST_CONSTRUCT,

    /**
     * The phase right before the processed bean's end-of-life.
     * <p>
     * The bean is ready to be deconstructed at this point, in a sense that it
     * will be ready-for-garbage-collection afterwards.
     */
    PRE_DESTROY
}
