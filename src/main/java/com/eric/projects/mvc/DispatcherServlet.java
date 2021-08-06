package com.eric.projects.mvc;

import com.eric.projects.mvc.handler.ControllerHandler;
import com.eric.projects.mvc.handler.Handler;
import com.eric.projects.mvc.handler.JspHandler;
import com.eric.projects.mvc.handler.PreRequestHandler;
import com.eric.projects.mvc.handler.SimpleUrlHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DispatcherServlet extends HttpServlet {

    private final List<Handler> HANDLER = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        HANDLER.add(new PreRequestHandler());
        HANDLER.add(new SimpleUrlHandler(getServletContext()));
        HANDLER.add(new JspHandler(getServletContext()));
        HANDLER.add(new ControllerHandler());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestHandlerChain handlerChain = new RequestHandlerChain(HANDLER.iterator(), req, resp);
        handlerChain.doHandlerChain();
        handlerChain.doRender();

    }
}
