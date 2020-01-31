package com.mantledillusion.injection.hura.core.lifecycle.misc;

import com.mantledillusion.injection.hura.core.InjectableInterface;

import java.util.function.BiPredicate;

public class Qualifier1to9PostfixPredicate implements BiPredicate<String, InjectableInterface> {

    @Override
    public boolean test(String s, InjectableInterface injectableInterface) {
        return s.matches(".*[1-9]");
    }
}
