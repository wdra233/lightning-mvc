package com.eric.projects.mvc.render;

import com.eric.projects.mvc.RequestHandlerChain;

public class DefaultRender implements Render {
    @Override
    public void render(RequestHandlerChain chain) throws Exception {
        int responseStatus = chain.getResponseStatus();
        chain.getResponse().setStatus(responseStatus);
    }
}
