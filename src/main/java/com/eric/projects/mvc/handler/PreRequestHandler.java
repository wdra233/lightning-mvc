package com.eric.projects.mvc.handler;

import com.eric.projects.mvc.RequestHandlerChain;
import lombok.extern.slf4j.Slf4j;

/**
 * pre-process http request, such as setting http code, print infos
 */
@Slf4j
public class PreRequestHandler implements Handler {

    @Override
    public boolean handle(RequestHandlerChain chain) throws Exception {
        chain.getRequest().setCharacterEncoding("UTF-8");
        String requestPath = chain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            chain.setRequestPath(requestPath.substring(0, requestPath.length() - 1));
        }
        log.info("[Lightning] {} {}", chain.getRequestMethod(), chain.getRequestPath());
        return true;
    }
}
