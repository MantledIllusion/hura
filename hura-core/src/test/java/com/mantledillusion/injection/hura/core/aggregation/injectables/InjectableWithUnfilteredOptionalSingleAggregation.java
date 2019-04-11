package com.mantledillusion.injection.hura.core.aggregation.injectables;

import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.instruction.Optional;

public class InjectableWithUnfilteredOptionalSingleAggregation {

    @Optional
    @Aggregate
    public Object singleton;
}
