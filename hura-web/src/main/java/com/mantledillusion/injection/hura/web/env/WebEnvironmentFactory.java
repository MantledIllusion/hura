package com.mantledillusion.injection.hura.web.env;

import javax.servlet.Servlet;

/**
 * Static factory containing methods that start builders for all standard {@link ServletContextConfiguration} implementations.
 */
public final class WebEnvironmentFactory {

    private WebEnvironmentFactory() {}

    /**
     * Starts a new builder for a {@link Servlet} registration.
     *
     * @param servletType The {@link Servlet}'s implementation type to register; might <b>not</b> be null.
     * @return A new {@link ServletRegistrationBuilder}, never null
     */
    public static ServletRegistrationBuilder registerServlet(Class<? extends Servlet> servletType) {
        if (servletType == null) {
            throw new IllegalArgumentException("Cannot begin registration of a null servlet type");
        }
        return new ServletRegistrationBuilder(servletType.getSimpleName(), servletType);
    }

    /**
     * Starts a new builder for a {@link Servlet} registration.
     *
     * @param name The {@link Servlet}'s name; might <b>not</b> be null.
     * @param servletType The {@link Servlet}'s implementation type to register; might <b>not</b> be null.
     * @return A new {@link ServletRegistrationBuilder}, never null
     */
    public static ServletRegistrationBuilder registerServlet(String name, Class<? extends Servlet> servletType) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot begin registration of an unnamed servlet");
        } else if (servletType == null) {
            throw new IllegalArgumentException("Cannot begin registration of a null servlet type");
        }
        return new ServletRegistrationBuilder(name, servletType);
    }
}
