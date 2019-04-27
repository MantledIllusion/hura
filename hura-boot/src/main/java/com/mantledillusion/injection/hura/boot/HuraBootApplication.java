package com.mantledillusion.injection.hura.boot;

import com.mantledillusion.injection.hura.boot.exception.BootException;
import com.mantledillusion.injection.hura.web.HuraServletContainerInitializer;
import com.mantledillusion.injection.hura.web.HuraWebApplicationInitializer;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class HuraBootApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuraBootApplication.class);
    private static final String REGEX_NUMERIC = "[1-9][0-9]*";
    private static final int MAX_PORT = 65535;

    public static final String PROP_SERVER_PORT = "hura.boot.server.port";

    public static final class HuraBootApplicationBuilder {

        private final Set<Class<? extends HuraWebApplicationInitializer>> initializerTypes;
        private Integer port;

        private HuraBootApplicationBuilder() {
            this.initializerTypes = new HashSet<>();
        }

        public synchronized HuraBootApplicationBuilder addApplication(Class<? extends HuraWebApplicationInitializer> initializerType) {
            if (initializerType == null) {
                throw new IllegalArgumentException("Cannot build an application using a null initializer");
            }
            this.initializerTypes.add(initializerType);
            return this;
        }

        public synchronized HuraBootApplicationBuilder setPort(int port) {
            if (port < 0 || port > MAX_PORT) {
                throw new IllegalArgumentException("Cannot set the port to a negative value or one bigger than " + MAX_PORT);
            }
            this.port = port;
            return this;
        }

        public synchronized HuraBootApplication startUp(String... args) {
            long ms = System.currentTimeMillis();

            try {
                ServletContainerInitializerInfo initializerInfo = new ServletContainerInitializerInfo(
                        HuraServletContainerInitializer.class, new HashSet<>(this.initializerTypes));

                DeploymentInfo servletBuilder = Servlets
                        .deployment()
                        .setClassLoader(HuraBootApplication.class.getClassLoader())
                        .setDeploymentName(HuraBootApplication.class.getSimpleName())
                        .setContextPath("/")
                        .addServletContainerInitializer(initializerInfo);

                DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
                manager.deploy();

                HttpHandler applicationHttpHandler = manager.start();
                // TODO redirect option
                //applicationHttpHandler = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", applicationHttpHandler);

                Undertow.Builder builder = Undertow.builder();
                // TODO SSL option
                // builder.addHttpsListener(determinePort(), "localhost", );
                builder.addHttpListener(determinePort(), "localhost");

                Undertow server = builder.setHandler(applicationHttpHandler).build();
                server.start();

                LOGGER.info("Started up web server in " + (System.currentTimeMillis()-ms) + " ms");

                return new HuraBootApplication(server);
            } catch (Exception e) {
                throw new BootException("Unable to start up application server", e);
            }
        }

        private int determinePort() {
            return this.port != null ? port : (System.getProperty(PROP_SERVER_PORT) != null &&
                    System.getProperty(PROP_SERVER_PORT).matches(REGEX_NUMERIC) ?
                    Integer.parseInt(System.getProperty(PROP_SERVER_PORT)) : 8080);
        }
    }

    private final Undertow server;

    private HuraBootApplication(Undertow server) {
        this.server = server;
    }

    public static HuraBootApplicationBuilder build(Class<? extends HuraWebApplicationInitializer> applicationInitializerType) {
        return new HuraBootApplicationBuilder().addApplication(applicationInitializerType);
    }
}
