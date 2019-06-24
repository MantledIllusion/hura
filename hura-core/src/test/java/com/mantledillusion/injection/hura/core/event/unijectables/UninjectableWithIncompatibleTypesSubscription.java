package com.mantledillusion.injection.hura.core.event.unijectables;

import com.mantledillusion.injection.hura.core.annotation.event.Subscribe;

public class UninjectableWithIncompatibleTypesSubscription {

    @Subscribe(Integer.class)
    public void receive(String event) {

    }
}
