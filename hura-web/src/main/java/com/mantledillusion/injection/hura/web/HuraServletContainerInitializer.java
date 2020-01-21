package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Injector;

import javax.servlet.*;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Hura Web's {@link ServletContainerInitializer}. Uses @{@link HandlesTypes} to inject, startup and destroy {@link HuraWebApplicationInitializer}s.
 */
@HandlesTypes({HuraWebApplicationInitializer.class})
public final class HuraServletContainerInitializer implements ServletContainerInitializer, ServletContextListener {

    private final Injector.RootInjector serverInjector;

    public HuraServletContainerInitializer() {
        this.serverInjector = Injector.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup(Set<Class<?>> applicationClasses, ServletContext ctx) throws ServletException {
        ctx.addListener(this);
        for (Class<?> c: applicationClasses) {
            if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers())
                    && HuraWebApplicationInitializer.class.isAssignableFrom(c)) {
                HuraWebApplication.LOGGER.debug("Starting up web application '" + c.getSimpleName() + "'");

                long ms = System.currentTimeMillis();

                Class<? extends HuraWebApplicationInitializer> applicationClass = (Class<? extends HuraWebApplicationInitializer>) c;
                HuraWebApplicationInitializer initializer = this.serverInjector.instantiate(applicationClass);

                HuraWebEnvironment webEnvironment = new HuraWebEnvironment(applicationClass.getPackage().getName(), applicationClass.getName());

                HuraWebApplication application = this.serverInjector.instantiate(HuraWebApplication.class, initializer, webEnvironment);
                application.configure(ctx);

                HuraWebApplication.LOGGER.info("Started up web application '" + c.getSimpleName() + "' in " + (System.currentTimeMillis()-ms) + "ms");
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        this.serverInjector.shutdown();
    }
}
