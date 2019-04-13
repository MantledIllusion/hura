package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Blueprint;

import java.util.ArrayList;
import java.util.List;

public interface HuraWebApplicationInitializer {

    final class HuraWebEnvironmentRegistration {

        private final List<Blueprint> blueprints;

        HuraWebEnvironmentRegistration() {
            this.blueprints =  new ArrayList<>();
        }

        List<Blueprint> getBlueprints() {
            return blueprints;
        }

        public HuraWebEnvironmentRegistration register(Blueprint blueprint) {
            this.blueprints.add(blueprint);
            return this;
        }
    }

    void configure(HuraWebEnvironmentRegistration registration);
}