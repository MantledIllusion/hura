package com.mantledillusion.injection.hura.core.aggregation.misc;

import com.mantledillusion.injection.hura.core.aggregation.injectables.InjectableWithName;
import com.mantledillusion.injection.hura.core.annotation.property.Property;

import java.util.function.BiPredicate;

public class PropertyDependentPredicate implements BiPredicate<String, InjectableWithName> {

    public static final String NAME_PROPERTY_ID = "nameProperty";

    @Property(NAME_PROPERTY_ID)
    private String name;

    @Override
    public boolean test(String s, InjectableWithName injectableWithName) {
        return name.equals(injectableWithName.name);
    }
}
