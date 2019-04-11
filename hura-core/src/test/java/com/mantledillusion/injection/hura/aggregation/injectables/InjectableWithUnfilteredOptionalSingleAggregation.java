package com.mantledillusion.injection.hura.aggregation.injectables;

import com.mantledillusion.injection.hura.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.annotation.instruction.Optional;

public class InjectableWithUnfilteredOptionalSingleAggregation {

    @Optional
    @Aggregate
    public Object singleton;
}
