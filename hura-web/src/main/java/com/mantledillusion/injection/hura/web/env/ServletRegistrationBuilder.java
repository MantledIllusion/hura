package com.mantledillusion.injection.hura.web.env;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;

import javax.servlet.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Builder for a {@link Blueprint.SingletonAllocation} to a {@link ServletContextConfiguration} that registers a specific {@link Servlet} with its configuration.
 * <p>
 * This builder can be used multiple times and is thread safe.
 */
public final class ServletRegistrationBuilder {

    private static final String SERVLET_REGISTRATION_QUALIFIER_PREFIX = "_servletConfiguration";

    private final String name;
    private final Class<? extends Servlet> servletType;
    private List<Consumer<ServletRegistration.Dynamic>> configurations;

    ServletRegistrationBuilder(String name, Class<? extends Servlet> servletType) {
        this.name = name;
        this.servletType = servletType;
        this.configurations = new ArrayList<>();
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#addMapping(String...)
     *
     * @param urlPatterns the URL patterns of the servlet mapping
     * @return this
     */
    public synchronized ServletRegistrationBuilder addMapping(String... urlPatterns) {
        this.configurations.add(dynamic -> dynamic.addMapping(urlPatterns));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setLoadOnStartup(int)
     *
     * @param loadOnStartup the initialization priority of the Servlet
     * @return this
     */
    public synchronized ServletRegistrationBuilder setLoadOnStartup(int loadOnStartup) {
        this.configurations.add(dynamic -> dynamic.setLoadOnStartup(loadOnStartup));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setAsyncSupported(boolean)
     *
     * @param isAsyncSupported true if the Servlet or Filter represented
     * by this dynamic Registration supports asynchronous operations,
     * false otherwise
     * @return this
     */
    public synchronized ServletRegistrationBuilder setAsyncSupported(boolean isAsyncSupported) {
        this.configurations.add(dynamic -> dynamic.setAsyncSupported(isAsyncSupported));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setRunAsRole(String)
     *
     * @param roleName the name of the <code>runAs</code> role
     * @return this
     */
    public synchronized ServletRegistrationBuilder setRunAsRole(String roleName) {
        this.configurations.add(dynamic -> dynamic.setRunAsRole(roleName));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setInitParameter(String, String)
     *
     * @param name the initialization parameter name
     * @param value the initialization parameter value
     * @return this
     */
    public synchronized ServletRegistrationBuilder setInitParameter(String name, String value) {
        this.configurations.add(dynamic -> dynamic.setInitParameter(name, value));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setInitParameters(Map)
     *
     * @param initParameters the initialization parameters
     * @return this
     */
    public synchronized ServletRegistrationBuilder setInitParameters(Map<String, String> initParameters) {
        this.configurations.add(dynamic -> dynamic.setInitParameters(initParameters));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setMultipartConfig(MultipartConfigElement)
     *
     * @param multipartConfig the {@link MultipartConfigElement} to be
     * applied to the patterns mapped to the registration
     * @return this
     */
    public synchronized ServletRegistrationBuilder setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.configurations.add(dynamic -> dynamic.setMultipartConfig(multipartConfig));
        return this;
    }

    /**
     * @see javax.servlet.ServletRegistration.Dynamic#setServletSecurity(ServletSecurityElement)
     *
     * @param constraint the {@link ServletSecurityElement} to be applied
     * to the patterns mapped to this ServletRegistration
     * @return this
     */
    public synchronized ServletRegistrationBuilder setMultipartConfig(ServletSecurityElement constraint) {
        this.configurations.add(dynamic -> dynamic.setServletSecurity(constraint));
        return this;
    }

    /**
     * Builds a {@link Blueprint.SingletonAllocation} containing a {@link ServletContextConfiguration} from the configuration specified until this point.
     *
     * @return A new {Blueprint.SingletonAllocation} instance, never null
     */
    public synchronized Blueprint.SingletonAllocation build() {
        String qualifier = SERVLET_REGISTRATION_QUALIFIER_PREFIX + UUID.randomUUID().toString();
        Blueprint.SingletonAllocation configurationSingleton = Blueprint.SingletonAllocation.allocateToInstance(qualifier, new ServletContextConfiguration() {

            private final String name = ServletRegistrationBuilder.this.name;
            private final Class<? extends Servlet> servletType = ServletRegistrationBuilder.this.servletType;
            private final List<Consumer<ServletRegistration.Dynamic>> configurations = ServletRegistrationBuilder.this.configurations;

            @Override
            public void configure(ServletContext ctx, Injector applicationInjector) {
                Servlet servlet = applicationInjector.instantiate(this.servletType);

                ServletRegistration.Dynamic dynamic = ctx.addServlet(this.name, servlet);
                this.configurations.forEach(config -> config.accept(dynamic));
            }
        });

        this.configurations = new ArrayList<>(this.configurations);

        return configurationSingleton;
    }
}
