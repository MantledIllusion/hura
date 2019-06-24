package com.mantledillusion.injection.hura.core;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.injection.Qualifier;
import com.mantledillusion.injection.hura.core.exception.EventException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Injectable event bus that allows exchanging event {@link Object}s of an arbitrary type between beans of the
 * same injection tree.
 * <p>
 * Every injection sequence receives its own bus. Depending on the property {@link #PROPERTY_BUS_ISOLATION}, that bus
 * will pass events published to it up to the bus of the parent injection sequence, which, when not isolated as well,
 * will also pass events up to the bus of its parent injection sequence and so on. When no bus in that chain is isolated,
 * the bus of the root injection sequence will be, so passing the published events on will stop there at the latest.
 * <p>
 * Instead of passing events on to their parent, isolated busses will publish events to their injection sequence's beans
 * and will instruct the busses of child injection sequences to do the same, cascading the published event back down.
 * <p>
 * Using bus isolation, the event publishing can easily be limited to a specific set of grouped beans.
 */
public class Bus {

    /**
     * The property key of the {@link Boolean} setting specifying whether the event bus of an injection sequence is
     * isolated, meaning that none of the events published through it will be dispatched to beans of parent injection
     * sequences.
     */
    public static final String PROPERTY_BUS_ISOLATION = "_busIsolation";
    static final String QUALIFIER_BACKBONE = "_eventBackbone";

    static final class EventBackbone {

        private final EventBackbone parentBackbone;
        private final Set<EventBackbone> childBackbones = synchronizedIdentitySet();
        private final Map<Class<?>, Set<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();
        private final boolean isolated;

        EventBackbone() {
            this(null, Boolean.TRUE.toString());
        }

        EventBackbone(EventBackbone parentBackbone, String isolate) {
            this.parentBackbone = parentBackbone;
            this.isolated = Boolean.parseBoolean(isolate);

            if (this.parentBackbone != null) {
                this.parentBackbone.childBackbones.add(this);
            }
        }

        void subscribe(Object bean, Method m) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            Set<Class<?>> eventTypes = new HashSet<>();
            Subscribe subscribe = m.getAnnotation(Subscribe.class);
            if (subscribe.value().length == 0) {
                eventTypes.add(parameterTypes[0]);
            } else {
                eventTypes.addAll(Arrays.asList(subscribe.value()));
            }

            boolean requiresEventInstance = parameterTypes.length > 0;
            Consumer<Object> subscriber = event -> {
                try {
                    if (requiresEventInstance) {
                        m.invoke(bean, event);
                    } else {
                        m.invoke(bean);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new EventException("Unable to deliver event of type '" + event.getClass().getSimpleName() +
                            "' to subscribed method '" + m.getName() + "'", e);
                }
            };

            eventTypes.forEach(eventType -> this.subscribers.
                    computeIfAbsent(eventType, type -> synchronizedIdentitySet()).add(subscriber));
        }

        void publish(Object event) {
            Class<?> eventType = event.getClass();
            while (eventType != null) {
                if (this.subscribers.containsKey(eventType)) {
                    this.subscribers.get(eventType).forEach(subscriber -> subscriber.accept(event));
                }
                eventType = eventType.getSuperclass();
            }
            this.childBackbones.forEach(child -> child.publish(event));
        }

        void detachFromParent() {
            this.parentBackbone.childBackbones.remove(this);
        }

        private static <T> Set<T> synchronizedIdentitySet() {
            return Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
        }
    }

    private final EventBackbone backbone;

    private Bus(@Inject @Qualifier(QUALIFIER_BACKBONE) EventBackbone backbone) {
        this.backbone = backbone;
    }

    /**
     * Publishes the given event.
     * <p>
     * No additional isolation will be used.
     *
     * @param event The event to publish; might <b>not</b> be null.
     */
    public void publish(Object event) {
        publish(event, false);
    }

    /**
     * Publishes the given event.
     *
     * @param event The event to publish; might <b>not</b> be null.
     * @param isolated Will isolate publishing to this busses' injection sequence.
     */
    public void publish(Object event, boolean isolated) {
        if (event == null) {
            throw new IllegalArgumentException("Cannot publish a null event");
        }
        EventBackbone backbone = this.backbone;
        if (!isolated) {
            while (!backbone.isolated && backbone.parentBackbone != null) {
                backbone = backbone.parentBackbone;
            }
        }
        backbone.publish(event);
    }
}
