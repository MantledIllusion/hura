package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.web.env.ServletContextConfiguration;

import javax.servlet.ServletContext;
import java.util.List;

class HuraWebApplication {

    @Inject
    private Injector injector;
    @Aggregate
    private List<ServletContextConfiguration> contextConfigurations;

    @Construct
    private HuraWebApplication() {}

    void configure(ServletContext ctx) {
        this.contextConfigurations.forEach(config -> config.configure(ctx, this.injector));
    }
}
