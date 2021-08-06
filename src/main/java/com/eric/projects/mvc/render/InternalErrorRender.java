package com.eric.projects.mvc.render;

import com.eric.projects.mvc.RequestHandlerChain;

import javax.servlet.http.HttpServletResponse;

public class InternalErrorRender implements Render {
    @Override
    public void render(RequestHandlerChain chain) throws Exception {
        chain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
