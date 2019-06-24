package com.mantledillusion.injection.hura.core.event.injectables;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;

public class InjectableWithExceptionThrowingSubscription {

    @Subscribe
    public void receive(Object event) {
        throw new RuntimeException();
    }
}
