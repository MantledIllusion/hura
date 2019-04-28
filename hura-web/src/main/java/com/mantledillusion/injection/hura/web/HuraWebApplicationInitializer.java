package com.mantledillusion.injection.hura.web;

import com.mantledillusion.injection.hura.core.Blueprint;

/**
 * {@link Blueprint} extension that defines the bean environment of a web application.
 * <p>
 * {@link com.mantledillusion.injection.hura.core.Blueprint.SingletonAllocation}s of the type
 * {@link com.mantledillusion.injection.hura.web.env.ServletContextConfiguration} that
 * are @{@link com.mantledillusion.injection.hura.core.annotation.instruction.Define}d by this {@link Blueprint}
 * are used to configure the {@link javax.servlet.ServletContext}.
 * <p>
 * The {@link com.mantledillusion.injection.hura.web.env.WebEnvironmentFactory} provides implementations of the
 * {@link com.mantledillusion.injection.hura.web.env.ServletContextConfiguration} interface for default use cases.
 */
public interface HuraWebApplicationInitializer extends Blueprint {

}