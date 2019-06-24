package com.mantledillusion.injection.hura.core.event.injectables;

import com.mantledillusion.injection.hura.core.Bus;
import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;

import java.util.ArrayList;
import java.util.List;

public class InjectableWithBusAndSubscription {

    @Inject
    public Bus bus;

    public final List<Object> events = new ArrayList<>();

    @Subscribe
    public void receive(Object o) {
        events.add(o);
    }
}
