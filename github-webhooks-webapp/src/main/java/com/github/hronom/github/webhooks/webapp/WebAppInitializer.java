package com.github.hronom.github.webhooks.webapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Conventions;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import com.github.hronom.github.webhooks.webapp.config.WebAppConfig;

public class WebAppInitializer implements WebApplicationInitializer {
    private final Logger logger = LogManager.getLogger();

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        logger.info("Launch servlet.");

        // Create the dispatcher servlet's Spring application context
        AnnotationConfigWebApplicationContext
            dispatcherContext
            = new AnnotationConfigWebApplicationContext();
        dispatcherContext.register(WebAppConfig.class);

        // Register filter to allow cross-domain requests
        Filter filter = new SimpleCORSFilter();
        String filterName = Conventions.getVariableName(filter);
        FilterRegistration.Dynamic filterRegistration = servletContext
            .addFilter(filterName, filter);
        filterRegistration.setAsyncSupported(true);
        filterRegistration
            .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // Register servlet
        ServletRegistration.Dynamic servletRegistration = servletContext
            .addServlet("githubwebhooks", new DispatcherServlet(dispatcherContext));
        servletRegistration.setLoadOnStartup(1);
        servletRegistration.addMapping("/api/*");

        logger.info("Servlet is ready.");
    }
}
