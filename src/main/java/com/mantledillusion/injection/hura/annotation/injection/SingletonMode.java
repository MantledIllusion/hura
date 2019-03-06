package com.mantledillusion.injection.hura.annotation.injection;

import com.mantledillusion.injection.hura.Injector;
import com.mantledillusion.injection.hura.annotation.instruction.Context;

/**
 * Mode that specifies from which injection context to retrieve a singleton.
 */
public enum SingletonMode {

    /**
     * Retrieve singletons from (and construct singletons to) the injection context
     * of the current injection sequence's injection sub tree.
     * <p>
     * Using this mode enables multiple different instances of the same
     * {@link Class} (instantiated by the same {@link Injector} in one injection
     * sequence each) to each have their own singleton instance while using the same
     * qualifier.
     * <p>
     * In addition, singleton instances of parent injection contexts will be
     * injected, but singletons introduced by child contexts do not bleed into
     * parent contexts.
     */
    SEQUENCE,

    /**
     * Retrieve singletons from the injection context of the injection tree's root.
     * <p>
     * Using this mode enables all beans injected by any {@link Injector} in any
     * injection sequence of the injection tree to share the same singleton by its
     * qualifier.
     * <p>
     * This mode is permitted for @{@link Context} sensitive types (such as
     * {@link Injector} itself), since it would allow context sensitive entities to
     * be taken out of their context.
     */
    GLOBAL
}
