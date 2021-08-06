package com.eric.projects.mvc.handler;

import com.eric.projects.Lightning;
import com.eric.projects.mvc.RequestHandlerChain;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * Process jsp request
 */
public class JspHandler implements Handler {

    private static final String JSP_SERVLET = "jsp";

    private RequestDispatcher jspServlet;

    public JspHandler(ServletContext servletContext) {
        jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (jspServlet == null) {
            throw new RuntimeException("No jsp servlet found");
        }
    }

    @Override
    public boolean handle(RequestHandlerChain chain) throws Exception {
        if (isPageView(chain.getRequestPath())) {
            jspServlet.forward(chain.getRequest(), chain.getResponse());
            return false;
        }
        return true;
    }

    private boolean isPageView(String url) {
        return url.startsWith(Lightning.getConfiguration().getViewPath());
    }
}
