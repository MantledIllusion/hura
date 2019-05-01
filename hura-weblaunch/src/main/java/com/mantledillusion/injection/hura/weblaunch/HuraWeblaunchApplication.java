package com.mantledillusion.injection.hura.weblaunch;

import com.mantledillusion.essentials.object.Null;
import com.mantledillusion.injection.hura.weblaunch.exception.WeblaunchException;
import com.mantledillusion.injection.hura.web.HuraServletContainerInitializer;
import com.mantledillusion.injection.hura.web.HuraWebApplicationInitializer;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The class representing a Hura Weblaunch application.
 * <p>
 * Use {@link #build(Class)} to configure, initialize and start up an application.
 */
public final class HuraWeblaunchApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuraWeblaunchApplication.class);
    private static final String REGEX_NUMERIC = "[1-9][0-9]*";
    private static final int MAX_PORT = 65535;

    public static final String PROP_SERVER_PORT = "hura.weblaunch.server.port";

    /**
     * Builder to initialize and {@link #startUp(String...)} a {@link HuraWeblaunchApplication}.
     */
    public static final class HuraWeblaunchApplicationBuilder {

        private final Set<Class<? extends HuraWebApplicationInitializer>> initializerTypes;
        private ResourceManager resourceManager = ResourceManager.EMPTY_RESOURCE_MANAGER;
        private Integer port;

        private HuraWeblaunchApplicationBuilder() {
            this.initializerTypes = new HashSet<>();
        }

        /**
         * Adds another {@link HuraWebApplicationInitializer} to start up to the configuration.
         *
         * @param initializerType Additional {@link HuraWebApplicationInitializer} implementation type; my <b>not</b> be null.
         * @return this
         */
        public synchronized HuraWeblaunchApplicationBuilder addApplication(Class<? extends HuraWebApplicationInitializer> initializerType) {
            if (initializerType == null) {
                throw new IllegalArgumentException("Cannot build an application using a null initializer");
            }
            this.initializerTypes.add(initializerType);
            return this;
        }

        /**
         * Sets the {@link ResourceManager} of the {@link Undertow} {@link DeploymentInfo}.
         *
         * @param resourceManager The {@link ResourceManager} to set; might <b>not</b> be null.
         * @return this
         */
        public synchronized HuraWeblaunchApplicationBuilder setResourceManager(ResourceManager resourceManager) {
            if (resourceManager == null) {
                throw new IllegalArgumentException("Cannot build an application using a null initializer");
            }
            this.resourceManager = resourceManager;
            return this;
        }

        /**
         * Sets the {@link Undertow} server's port.
         *
         * @param port The port to use; may not be &lt;0 or &gt;{@link #MAX_PORT}.
         * @return this
         */
        public synchronized HuraWeblaunchApplicationBuilder setPort(int port) {
            if (port < 0 || port > MAX_PORT) {
                throw new IllegalArgumentException("Cannot set the port to a negative value or one bigger than " + MAX_PORT);
            }
            this.port = port;
            return this;
        }

        /**
         * Uses the configuration stored in this builder upto this point to start up all {@link HuraWebApplicationInitializer}s configured.
         *
         * @param args Java Program parameters; might be null.
         * @return A new {@link HuraWeblaunchApplication} instance, never null
         */
        public synchronized HuraWeblaunchApplication startUp(String... args) {
            long ms = System.currentTimeMillis();

            try {
                ServletContainerInitializerInfo initializerInfo = new ServletContainerInitializerInfo(
                        HuraServletContainerInitializer.class, new HashSet<>(this.initializerTypes));

                DeploymentInfo deployment = Servlets
                        .deployment()
                        .setClassLoader(HuraWeblaunchApplication.class.getClassLoader())
                        .setDeploymentName(HuraWeblaunchApplication.class.getSimpleName())
                        .setContextPath("/")
                        .addServletContainerInitializer(initializerInfo)
                        .setResourceManager(this.resourceManager);

                DeploymentManager manager = Servlets.defaultContainer().addDeployment(deployment);
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

                HuraWeblaunchApplication app = new HuraWeblaunchApplication(server);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> app.shutdown()));

                return app;
            } catch (Exception e) {
                throw new WeblaunchException("Unable to start up application server", e);
            }
        }

        private int determinePort() {
            return this.port != null ? port : (System.getProperty(PROP_SERVER_PORT) != null &&
                    System.getProperty(PROP_SERVER_PORT).matches(REGEX_NUMERIC) ?
                    Integer.parseInt(System.getProperty(PROP_SERVER_PORT)) : 8080);
        }
    }

    private final Undertow server;

    private HuraWeblaunchApplication(Undertow server) {
        this.server = server;
    }

    private void shutdown() {
        long ms = System.currentTimeMillis();
        try {
            this.server.stop();
            LOGGER.info("Stopped web server in " + (System.currentTimeMillis()-ms) + " ms");
        } catch (Exception e) {
            LOGGER.error("Unable to cleanly stop web server", e);
        }
    }

    /**
     * Begins a new {@link HuraWeblaunchApplicationBuilder}.
     *
     * @param applicationInitializerType The first (and possibly only) {@link HuraWebApplicationInitializer}; might <b>not</b> be null.
     * @return A new {@link HuraWeblaunchApplicationBuilder} instance, never null
     */
    public static HuraWeblaunchApplicationBuilder build(Class<? extends HuraWebApplicationInitializer> applicationInitializerType) {
        return new HuraWeblaunchApplicationBuilder().addApplication(applicationInitializerType);
    }
}
