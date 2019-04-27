package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Injector;
import com.mantledillusion.injection.hura.core.annotation.injection.Aggregate;
import com.mantledillusion.injection.hura.core.annotation.injection.Inject;
import com.mantledillusion.injection.hura.core.annotation.instruction.Construct;
import com.mantledillusion.injection.hura.web.env.ServletContextConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.List;

class HuraWebApplication {

    static final Logger LOGGER = LoggerFactory.getLogger(HuraWebApplication.class);

    @Inject
    private Injector injector;
    @Aggregate
    private List<ServletContextConfiguration> contextConfigurations;

    @Construct
    private HuraWebApplication() {}

    void configure(ServletContext ctx) {
        this.contextConfigurations.forEach(config -> {
            LOGGER.debug("-> Configuring " + ServletContext.class.getSimpleName()
                    + " for " + ServletContextConfiguration.class
                    + " '" + config.getClass().getSimpleName() + "'");
            config.configure(ctx, this.injector);
        });
    }
}
