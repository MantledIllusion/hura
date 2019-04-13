package com.mantledillusion.injection.hura.web.env;

import com.mantledillusion.injection.hura.core.Injector;

import javax.servlet.ServletContext;

public interface ServletContextConfiguration {

    void configure(ServletContext ctx, Injector applicationInjector);
}