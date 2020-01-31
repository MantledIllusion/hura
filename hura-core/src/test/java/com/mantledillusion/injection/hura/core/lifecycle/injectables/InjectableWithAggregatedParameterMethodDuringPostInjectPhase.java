package com.mantledillusion.injection.hura.core.lifecycle.injectables;

import com.mantledillusion.injection.hura.core.InjectableInterface;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.lifecycle.bean.PostInject;

import java.util.List;

public class InjectableWithAggregatedParameterMethodDuringPostInjectPhase {

    @PostInject
    public void process(@Aggregate List<InjectableInterface> aggregated) {
    }
}
