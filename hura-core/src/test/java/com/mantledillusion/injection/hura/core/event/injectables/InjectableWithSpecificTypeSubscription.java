package com.mantledillusion.injection.hura.core.event.injectables;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class InjectableWithSpecificTypeSubscription {

    public static class Event {

    }

    public final List<Event> events = new ArrayList<>();

    @Subscribe
    public void receive(Event e) {
        events.add(e);
    }
}
