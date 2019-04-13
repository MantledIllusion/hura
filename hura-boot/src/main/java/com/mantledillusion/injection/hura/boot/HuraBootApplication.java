package com.mantledillusion.injection.hura.boot;

import com.mantledillusion.injection.hura.boot.exception.BootException;
import com.mantledillusion.injection.hura.web.HuraServletContainerInitializer;
import com.mantledillusion.injection.hura.web.HuraWebApplicationInitializer;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;

import java.util.HashSet;
import java.util.Set;

public final class HuraBootApplication {

    public static final class HuraBootApplicationBuilder {

        private final Set<Class<? extends HuraWebApplicationInitializer>> initializerTypes;

        private HuraBootApplicationBuilder() {
            this.initializerTypes = new HashSet<>();
        }

        public synchronized HuraBootApplicationBuilder addInitializer(Class<? extends HuraWebApplicationInitializer> initializerType) {
            if (initializerType == null) {
                throw new IllegalArgumentException("Cannot build an application using a null initializer");
            }
            this.initializerTypes.add(initializerType);
            return this;
        }

        public synchronized HuraBootApplication startUp() {
            try {
                DeploymentInfo servletBuilder = Servlets
                        .deployment()
                        .setClassLoader(HuraBootApplication.class.getClassLoader())
                        .setDeploymentName("abc")
                        .setContextPath("/abc")
                        .addServletContainerInitializer(new ServletContainerInitializerInfo(HuraServletContainerInitializer.class, new HashSet<>(this.initializerTypes)));

                DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
                manager.deploy();

                PathHandler path = Handlers.path(Handlers.redirect("/myAbcApplication")).addPrefixPath("/myAbcApplication", manager.start());
                Undertow server = Undertow.builder().addHttpListener(8080, "localhost").setHandler(path).build();
                server.start();

                return new HuraBootApplication(server);
            } catch (Exception e) {
                throw new BootException("Unable to start up application server", e);
            }
        }
    }

    private final Undertow server;

    private HuraBootApplication(Undertow server) {
        this.server = server;
    }

    public static HuraBootApplicationBuilder build(Class<? extends HuraWebApplicationInitializer> initializerType) {
        return new HuraBootApplicationBuilder().addInitializer(initializerType);
    }
}
