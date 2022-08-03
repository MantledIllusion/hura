package com.mantledillusion.injection.hura.web.env;

import com.mantledillusion.injection.hura.core.Blueprint;
import com.mantledillusion.injection.hura.core.Injector;

import javax.servlet.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Builder for a {@link Blueprint.SingletonAllocation} to a {@link ServletContextConfiguration} that registers a specific {@link Filter} with its configuration.
 * <p>
 * This builder can be used multiple times and is thread safe.
 */
public final class FilterRegistrationBuilder {

    private static final String FILTER_REGISTRATION_QUALIFIER_PREFIX = "_filterConfiguration";

    private final String name;
    private final Class<? extends Filter> filterType;
    private List<Consumer<FilterRegistration.Dynamic>> configurations;

    FilterRegistrationBuilder(String name, Class<? extends Filter> filterType) {
        this.name = name;
        this.filterType = filterType;
        this.configurations = new ArrayList<>();
    }

    /**
     * @see FilterRegistration.Dynamic#addMappingForServletNames(EnumSet, boolean, String...)
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     * or null if the default {@link DispatcherType#REQUEST} is to be used
     * @param isMatchAfter true if the given filter mapping should be matched
     * after any declared filter mappings, and false if it is supposed to
     * be matched before any declared filter mappings of the ServletContext
     * from which this FilterRegistration was obtained
     * @param servletNames the servlet names of the filter mapping
     * @return this
     */
    public synchronized FilterRegistrationBuilder addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        this.configurations.add(dynamic -> dynamic.addMappingForServletNames(dispatcherTypes, isMatchAfter, servletNames));
        return this;
    }

    /**
     * @see FilterRegistration.Dynamic#addMappingForUrlPatterns(EnumSet, boolean, String...)
     *
     * @param dispatcherTypes the dispatcher types of the filter mapping,
     * or null if the default {@link DispatcherType#REQUEST} is to be used
     * @param isMatchAfter true if the given filter mapping should be matched
     * after any declared filter mappings, and false if it is supposed to
     * be matched before any declared filter mappings of the ServletContext
     * from which this FilterRegistration was obtained
     * @param urlPatterns the url patterns of the filter mapping
     * @return this
     */
    public synchronized FilterRegistrationBuilder addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        this.configurations.add(dynamic -> dynamic.addMappingForUrlPatterns(dispatcherTypes, isMatchAfter, urlPatterns));
        return this;
    }

    /**
     * @see FilterRegistration.Dynamic#setAsyncSupported(boolean)
     *
     * @param isAsyncSupported true if the Servlet or Filter represented
     * by this dynamic Registration supports asynchronous operations,
     * false otherwise
     * @return this
     */
    public synchronized FilterRegistrationBuilder setAsyncSupported(boolean isAsyncSupported) {
        this.configurations.add(dynamic -> dynamic.setAsyncSupported(isAsyncSupported));
        return this;
    }

    /**
     * @see FilterRegistration.Dynamic#setInitParameter(String, String)
     *
     * @param name the initialization parameter name
     * @param value the initialization parameter value
     * @return this
     */
    public synchronized FilterRegistrationBuilder setInitParameter(String name, String value) {
        this.configurations.add(dynamic -> dynamic.setInitParameter(name, value));
        return this;
    }

    /**
     * @see ServletRegistration.Dynamic#setInitParameters(Map)
     *
     * @param initParameters the initialization parameters
     * @return this
     */
    public synchronized FilterRegistrationBuilder setInitParameters(Map<String, String> initParameters) {
        this.configurations.add(dynamic -> dynamic.setInitParameters(initParameters));
        return this;
    }

    /**
     * Builds a {@link Blueprint.SingletonAllocation} containing a {@link ServletContextConfiguration} from the configuration specified until this point.
     *
     * @return A new {Blueprint.SingletonAllocation} instance, never null
     */
    public synchronized Blueprint.SingletonAllocation build() {
        String qualifier = FILTER_REGISTRATION_QUALIFIER_PREFIX + UUID.randomUUID().toString();
        Blueprint.SingletonAllocation configurationSingleton = Blueprint.SingletonAllocation.allocateToInstance(qualifier, new ServletContextConfiguration() {

            private final String name = FilterRegistrationBuilder.this.name;
            private final Class<? extends Filter> filterType = FilterRegistrationBuilder.this.filterType;
            private final List<Consumer<FilterRegistration.Dynamic>> configurations = FilterRegistrationBuilder.this.configurations;

            @Override
            public void configure(ServletContext ctx, Injector applicationInjector) {
                Filter filter = applicationInjector.instantiate(this.filterType);

                FilterRegistration.Dynamic dynamic = ctx.addFilter(this.name, filter);
                this.configurations.forEach(config -> config.accept(dynamic));
            }
        });

        this.configurations = new ArrayList<>(this.configurations);

        return configurationSingleton;
    }
}
