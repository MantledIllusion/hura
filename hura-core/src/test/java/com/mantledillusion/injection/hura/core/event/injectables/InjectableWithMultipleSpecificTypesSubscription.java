package com.mantledillusion.injection.hura.core.event.injectables;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class InjectableWithMultipleSpecificTypesSubscription {

    public static class AbstractEvent {

    }

    public static class EventA extends AbstractEvent {

    }

    public static class EventB extends AbstractEvent {

    }

    public static class EventC extends AbstractEvent {

    }

    public final List<AbstractEvent> events = new ArrayList<>();

    @Subscribe({EventA.class, EventC.class})
    public void receive(AbstractEvent e) {
        events.add(e);
    }
}
