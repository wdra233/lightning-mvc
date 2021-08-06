package com.eric.projects.mvc.render;

import com.eric.projects.mvc.RequestHandlerChain;

public interface Render {

    void render(RequestHandlerChain chain) throws Exception;
}
