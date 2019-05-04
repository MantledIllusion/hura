package com.mantledillusion.injection.hura.weblaunch;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
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
        private SSLContext sslContext;

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
         * Enables SSL for the {@link Undertow} HTTPS listener using the given certificate.
         * <p>
         * Uses {@link #setSslCertificate(File, String, String)} for X.509 certificates.
         *
         * @param x509Certificate The X.509 certificate file to load; might <b>not</b> be null or return either {@link File#isFile()} = false or {@link File#exists()} = false.
         * @return this
         */
        public synchronized HuraWeblaunchApplicationBuilder setSslCertificate(File x509Certificate) {
            return setSslCertificate(x509Certificate, "X.509", TrustManagerFactory.getDefaultAlgorithm());
        }

        /**
         * Enables SSL for the {@link Undertow} HTTPS listener using the given certificate.
         *
         * @param certificate The certificate file to load; might <b>not</b> be null or return either {@link File#isFile()} = false or {@link File#exists()} = false.
         * @param certificateType The type of the certificate; might <b>not</b> be null and has to describe a known algorithm.
         * @param tmfAlgorithm The algorithm type of the {@link TrustManagerFactory}, which should match the certificate type; might <b>not</b> be null and has to describe a known algorithm.
         * @return this
         */
        public synchronized HuraWeblaunchApplicationBuilder setSslCertificate(File certificate, String certificateType, String tmfAlgorithm) {
            if (certificate == null || !certificate.exists() || !certificate.isFile()) {
                throw new IllegalArgumentException("Cannot load a SSL certificate that is either null, does not exist in the file system or is no file.");
            }

            CertificateFactory cf;
            try {
                cf = CertificateFactory.getInstance(certificateType);
            } catch (CertificateException e) {
                throw new IllegalArgumentException("Cannot load a SSL certificate with a non-matching certificate type", e);
            }

            TrustManagerFactory tmf;
            try {
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("Cannot load a SSL certificate with a non-matching trust manager factory algorithm", e);
            }

            InputStream is = null;
            try {
                is = new FileInputStream(certificate);

                KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(null);

                Certificate caCert = cf.generateCertificate(is);
                ks.setCertificateEntry("caCert", caCert);

                tmf.init(ks);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);
                this.sslContext = sslContext;
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot load the given SSL certificate into an SSL context", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new IllegalStateException("Cannot close the file input stream to the given SSL certificate", e);
                    }
                }
            }
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

                if (this.sslContext == null) {
                    builder.addHttpListener(determinePort(), "localhost");
                } else {
                    builder.addHttpsListener(determinePort(), "localhost", this.sslContext, null);
                }

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
