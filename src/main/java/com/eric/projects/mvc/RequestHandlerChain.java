package com.eric.projects.mvc;

import com.eric.projects.mvc.handler.Handler;
import com.eric.projects.mvc.render.DefaultRender;
import com.eric.projects.mvc.render.InternalErrorRender;
import com.eric.projects.mvc.render.Render;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

@Data
@Slf4j
public class RequestHandlerChain {

    private Iterator<Handler> handlerIterators;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private String requestMethod;

    private String requestPath;

    private int responseStatus;

    private Render render;

    public RequestHandlerChain(Iterator<Handler> handlerIterators, HttpServletRequest request, HttpServletResponse response) {
        this.handlerIterators = handlerIterators;
        this.request = request;
        this.response = response;
        this.requestMethod = request.getMethod();
        this.requestPath = request.getPathInfo();
        this.responseStatus = HttpServletResponse.SC_OK;
    }

    public void doHandlerChain() {
        try {
            while (handlerIterators.hasNext()) {
                if (!handlerIterators.next().handle(this)) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("doHandlerChain error", e);
            render = new InternalErrorRender();
        }
    }

    public void doRender() {
        if (render == null) {
            render = new DefaultRender();
        }
        try {
            render.render(this);
        } catch (Exception e) {
            log.error("doRender", e);
            throw new RuntimeException(e);
        }
    }
}
