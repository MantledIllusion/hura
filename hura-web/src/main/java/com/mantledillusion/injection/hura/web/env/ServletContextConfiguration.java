package com.mantledillusion.injection.hura.web.env;

import com.mantledillusion.injection.hura.core.Injector;

import javax.servlet.ServletContext;

/**
 * Interface for configurations of {@link ServletContext}s.
 * <p>
 * Implementations of this interface that are @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}d
 * as {@link com.mantledillusion.injection.hura.core.Blueprint.SingletonAllocation}s in
 * {@link com.mantledillusion.injection.hura.web.HuraWebApplicationInitializer}s are called when the {@link ServletContext} is initialized.
 * <p>
 * The {@link WebEnvironmentFactory} provides builders for standard use case implementations.
 */
public interface ServletContextConfiguration {

    /**
     * Configures the {@link ServletContext}.
     *
     * @param ctx                 The {@link ServletContext} to configure; might <b>not</b> be null.
     * @param applicationInjector The {@link Injector} of the web application the @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}ing
     *                            {@link com.mantledillusion.injection.hura.web.HuraWebApplicationInitializer} initializes. Can be used to instantiate
     *                            {@link javax.servlet.Servlet}s or other web application instances that need to be configured; might <b>not</b> be null.
     */
    void configure(ServletContext ctx, Injector applicationInjector);
}