package com.mantledillusion.injection.hura.core.event.unijectables;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;

public class UninjectableWithMultipleParameterSubscription {

    @Subscribe
    public void receive(Object o1, Object o2) {

    }
}
