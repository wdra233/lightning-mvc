package com.eric.projects.mvc.handler;

import com.eric.projects.Lightning;
import com.eric.projects.mvc.RequestHandlerChain;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * process normal http request
 * mainly process static resource
 */
@Slf4j
public class SimpleUrlHandler implements Handler {

    private static final String TOMCAT_DEFAULT_SERVLET = "default";

    private RequestDispatcher defaultServlet;

    public SimpleUrlHandler(ServletContext servletContext) {
        defaultServlet = servletContext.getNamedDispatcher(TOMCAT_DEFAULT_SERVLET);

        if (defaultServlet == null) {
            throw new RuntimeException("No default servlet found!");
        }

        log.info("The default servlet for serving static resource is [{}]", TOMCAT_DEFAULT_SERVLET);
    }

    @Override
    public boolean handle(final RequestHandlerChain chain) throws Exception {
        if (isStaticResource(chain.getRequestPath())) {
            defaultServlet.forward(chain.getRequest(), chain.getResponse());
            return false;
        }
        return true;
    }

    private boolean isStaticResource(String url) {
        return url.startsWith(Lightning.getConfiguration().getAssetPath());
    }
}
