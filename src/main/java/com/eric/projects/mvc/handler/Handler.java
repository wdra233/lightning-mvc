package com.eric.projects.mvc.handler;

import com.eric.projects.mvc.RequestHandlerChain;

public interface Handler {

    boolean handle(final RequestHandlerChain chain) throws Exception;
}
